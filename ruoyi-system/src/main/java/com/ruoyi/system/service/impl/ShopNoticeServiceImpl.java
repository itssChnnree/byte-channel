package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.constant.ShopNoticeTagConstant;
import com.ruoyi.system.domain.dto.ShopNoticeDto;
import com.ruoyi.system.domain.entity.ShopNoticeFile;
import com.ruoyi.system.domain.vo.ShopNoticeFileVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.ShopNoticeFileMapper;
import com.ruoyi.system.mapper.ShopNoticeMapper;
import com.ruoyi.system.mapstruct.ShopNoticeMapstruct;
import com.ruoyi.system.service.IShopNoticeService;
import com.ruoyi.system.domain.entity.ShopNotice;
import com.ruoyi.system.domain.vo.ShopNoticeVo;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 商城公告表(ShopNotice)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:38:29
 */
@Service("shopNoticeService")
public class ShopNoticeServiceImpl implements IShopNoticeService {

    @Resource
    private  ShopNoticeMapper shopNoticeMapper;

    @Resource
    private ShopNoticeFileMapper shopNoticeFileMapper;

    @Resource
    private ShopNoticeMapstruct shopNoticeMapstruct;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 教程查询Redis Key前缀
     */
    private static final String TUTORIAL_CURSOR_KEY_PREFIX = "tutorial:cursor:";

    /**
     * 每次查询数量
     */
    private static final int TUTORIAL_PAGE_SIZE = 6;

    /**
     * Redis缓存过期时间（7天）
     */
    private static final long TUTORIAL_CURSOR_EXPIRE_DAYS = 7;

    /**
     * [新增公告]
     *
     * @param shopNoticeDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/23
     **/
    @Override
    @Transactional
    public Result add(ShopNoticeDto shopNoticeDto) {
        ShopNotice shopNotice = shopNoticeMapstruct.changeDto2(shopNoticeDto);
        if ("2".equals(shopNotice.getNoticeType()) || "3".equals(shopNotice.getNoticeType())) {
            if (StrUtil.isBlank(shopNotice.getNoticeContent())){
                return Result.fail("请填写内容");
            }
        }
        if ("3".equals(shopNotice.getNoticeType())) {
            if(!ShopNoticeTagConstant.SECTIONS.contains(shopNotice.getTag())){
                return Result.fail("标签不存在");
            }
            if(StrUtil.isBlank(shopNoticeDto.getDocumentDescription())){
                return Result.fail("请输入文档描述");
            }
        }
        int insert = shopNoticeMapper.insert(shopNotice);
        if (insert <= 0){
            return Result.fail("新增文档失败");
        }
        if (!CollectionUtils.isEmpty(shopNoticeDto.getFileUrls())){
            if (shopNoticeDto.getFileUrls().size() > 5){
                return Result.fail("最多上传5个附件");
            }
            //生成文件对象
            List<ShopNoticeFile> shopNoticeFiles = shopNoticeDto.getFileUrls().stream().map(fileDto ->
                    ShopNoticeFile.builder()
                        .shopNoticeId(shopNotice.getId())
                        .fileUrl(fileDto.getFileUrl())
                        .fileName(fileDto.getFileName())
                        .build()).collect(Collectors.toList());
            int insertBatch = shopNoticeFileMapper.insertBatch(shopNoticeFiles);
        }
        return Result.success(shopNotice);
    }

    /**
     * [根据公告id查询公告-用户]
     *
     * @param id 公告id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/24
     **/
    @Override
    public Result<ShopNoticeVo> getById(String id) {
        ShopNoticeVo shopNoticeVo = shopNoticeMapper.queryById(id);
        if (shopNoticeVo == null){
            return Result.fail("公告不存在");
        }
        List<ShopNoticeFileVo> shopNoticeFileVos = shopNoticeFileMapper.selectList(shopNoticeVo.getId());
        shopNoticeVo.setShopNoticeFiles(shopNoticeFileVos);
        return Result.success(shopNoticeVo);
    }

    /**
     * [后台查询公告详情]
     *
     * @param id 公告id
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.vo.ShopNoticeVo>
     * @author 陈湘岳 2025/11/27
     **/
    @Override
    public Result<ShopNoticeVo> getByIdSystem(String id) {
        ShopNoticeVo shopNoticeVo = shopNoticeMapper.getByIdSystem(id);
        if (shopNoticeVo == null){
            return Result.fail("公告不存在");
        }
        List<ShopNoticeFileVo> shopNoticeFileVos = shopNoticeFileMapper.selectList(shopNoticeVo.getId());
        shopNoticeVo.setShopNoticeFiles(shopNoticeFileVos);
        return Result.success(shopNoticeVo);
    }


    /**
     * [变更公告状态]
     *
     * @param id 公告id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/27
     **/
    @Override
    public Result changeStatus(String id) {
        ShopNotice shopNotice = shopNoticeMapper.selectById(id);
        if (shopNotice == null){
            return Result.fail("公告不存在");
        }
        if ("1".equals(shopNotice.getStatus())){
            shopNotice.setStatus("0");
        }else {
            shopNotice.setStatus("1");
        }
        int i = shopNoticeMapper.updateById(shopNotice);
        return Result.success(shopNotice);
    }

    /**
     * [更新公告]
     *
     * @param shopNoticeDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/24
     **/
    @Override
    public Result update(ShopNoticeDto shopNoticeDto) {
        ShopNotice shopNotice = shopNoticeMapstruct.changeDto2(shopNoticeDto);
        if ("3".equals(shopNotice.getNoticeType())) {
            if(!ShopNoticeTagConstant.SECTIONS.contains(shopNotice.getTag())){
                return Result.fail("标签不存在");
            }
            if(StrUtil.isBlank(shopNoticeDto.getDocumentDescription())){
                return Result.fail("请输入文档描述");
            }
        }
        int i = shopNoticeMapper.updateById(shopNotice);
        if(i <= 0){
            return Result.fail("更新失败");
        }
        shopNoticeFileMapper.deleteByShopNoticeId(shopNotice.getId());
        if (!CollectionUtils.isEmpty(shopNoticeDto.getFileUrls())){
            if (shopNoticeDto.getFileUrls().size() > 5){
                return Result.fail("最多上传5个附件");
            }
            //生成文件对象
            List<ShopNoticeFile> shopNoticeFiles = shopNoticeDto.getFileUrls().stream().map(fileDto ->
                    ShopNoticeFile.builder()
                            .shopNoticeId(shopNotice.getId())
                            .fileUrl(fileDto.getFileUrl())
                            .fileName(fileDto.getFileName())
                            .build()).collect(Collectors.toList());
            int insertBatch = shopNoticeFileMapper.insertBatch(shopNoticeFiles);
        }
        return Result.success(shopNotice);
    }

    /**
     * [分页查询]
     *
     * @param shopNoticeDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/23
     **/
    @Override
    public Result list(ShopNoticeDto shopNoticeDto) {
        PageHelper.startPage(shopNoticeDto);
        List<ShopNoticeVo> shopNoticeVos = shopNoticeMapper.selectList(shopNoticeDto);
        return Result.success(new PageInfo<>(shopNoticeVos));
    }

    /**
     * [分页查询]
     *
     * @param shopNoticeDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/23
     **/
    @Override
    public Result userlist(ShopNoticeDto shopNoticeDto) {
        PageHelper.startPage(shopNoticeDto);
        List<ShopNoticeVo> shopNoticeVos = shopNoticeMapper.userlist(shopNoticeDto);
        return Result.success(new PageInfo<>(shopNoticeVos));
    }


    /**
     * [查询公告标题列表]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/23
     **/
    @Override
    public Result titleList() {
        List<ShopNoticeVo> shopNoticeVos = shopNoticeMapper.titleList();
        return Result.success(shopNoticeVos);
    }


    /**
     * [根据公告id删除公告]
     *
     * @param id 公告id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/11/24
     **/
    @Override
    public Result deleteById(String id) {
        int i = shopNoticeMapper.deleteById(id);
        if (i <= 0){
            return Result.fail("删除失败");
        }
        shopNoticeFileMapper.deleteByShopNoticeId(id);
        return Result.success("删除成功");
    }

    /**
     * [查询教程列表-用户维度去重]
     * 使用Redis游标记录用户查询进度，每轮查询不重复，支持sort重复场景
     *
     * @param userId 用户ID
     * @param isNew 是否更新游标查询新数据，true更新游标并查询，false使用当前游标查询
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/23
     **/
    @Override
    public Result tutorialList(String userId, Boolean isNew) {
        try {
            // 1. 构建Redis Key
            String cursorKey = TUTORIAL_CURSOR_KEY_PREFIX + userId;
            Integer lastSort = null;
            String lastId = null;

            // 2. 如果需要更新游标（isNew=true），先查询当前数据并更新游标
            if (Boolean.TRUE.equals(isNew)) {
                // 2.1 获取当前游标值
                String cursorValue = stringRedisTemplate.opsForValue().get(cursorKey);
                Integer currentSort = null;
                String currentId = null;

                if (StrUtil.isNotBlank(cursorValue)) {
                    String[] parts = cursorValue.split(":");
                    if (parts.length == 2) {
                        try {
                            currentSort = Integer.valueOf(parts[0]);
                            currentId = parts[1];
                        } catch (NumberFormatException e) {
                            LogEsUtil.warn("解析教程游标失败，用户id: " + userId + ", 游标值: " + cursorValue);
                        }
                    }
                }

                // 2.2 查询当前游标位置的数据（用于更新游标）
                List<ShopNoticeVo> currentTutorials = shopNoticeMapper.selectTutorialListByCursor(currentSort, currentId, TUTORIAL_PAGE_SIZE);

                // 2.3 如果查询到数据，更新游标到最后一条
                if (!CollectionUtils.isEmpty(currentTutorials)) {
                    ShopNoticeVo lastTutorial = currentTutorials.get(currentTutorials.size() - 1);
                    String newCursor = lastTutorial.getSort() + ":" + lastTutorial.getId();
                    stringRedisTemplate.opsForValue().set(cursorKey, newCursor, TUTORIAL_CURSOR_EXPIRE_DAYS, TimeUnit.DAYS);
                    LogEsUtil.info("更新教程游标，用户id: " + userId + ", 新游标: " + newCursor);

                    // 2.4 如果当前查询结果不足6条，说明一轮结束，重置游标
                    if (currentTutorials.size() < TUTORIAL_PAGE_SIZE) {
                        stringRedisTemplate.delete(cursorKey);
                        LogEsUtil.info("用户教程查询一轮结束，重置游标，用户id: " + userId);
                    }
                } else if (currentSort != null) {
                    // 当前游标位置无数据，一轮结束，重置游标
                    stringRedisTemplate.delete(cursorKey);
                    LogEsUtil.info("用户教程查询一轮结束，重置游标，用户id: " + userId);
                }

                // 2.5 获取更新后的游标值用于本次查询
                String updatedCursorValue = stringRedisTemplate.opsForValue().get(cursorKey);
                if (StrUtil.isNotBlank(updatedCursorValue)) {
                    String[] parts = updatedCursorValue.split(":");
                    if (parts.length == 2) {
                        try {
                            lastSort = Integer.valueOf(parts[0]);
                            lastId = parts[1];
                        } catch (NumberFormatException e) {
                            LogEsUtil.warn("解析更新后游标失败，用户id: " + userId);
                        }
                    }
                }
            } else {
                // 3. isNew=false，使用当前游标查询，不更新游标
                String cursorValue = stringRedisTemplate.opsForValue().get(cursorKey);
                if (StrUtil.isNotBlank(cursorValue)) {
                    String[] parts = cursorValue.split(":");
                    if (parts.length == 2) {
                        try {
                            lastSort = Integer.valueOf(parts[0]);
                            lastId = parts[1];
                        } catch (NumberFormatException e) {
                            LogEsUtil.warn("解析教程游标失败，用户id: " + userId + ", 游标值: " + cursorValue);
                        }
                    }
                }
            }

            // 4. 查询教程列表（复合游标分页）
            List<ShopNoticeVo> tutorials = shopNoticeMapper.selectTutorialListByCursor(lastSort, lastId, TUTORIAL_PAGE_SIZE);

            return Result.success(tutorials);

        } catch (Exception e) {
            LogEsUtil.error("查询教程列表失败，用户id: " + userId, e);
            return Result.fail("查询教程列表失败: " + e.getMessage());
        }
    }
}
