package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
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
        if ("2".equals(shopNotice.getNoticeType())){
            if (StrUtil.isBlank(shopNotice.getNoticeContent())){
                return Result.fail("请填写公告内容");
            }
        }
        int insert = shopNoticeMapper.insert(shopNotice);
        if (insert <= 0){
            return Result.fail("新增公告失败");
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
}
