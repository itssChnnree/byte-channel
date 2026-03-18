package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.domain.dto.ShopNoticeDto;
import com.ruoyi.system.domain.dto.SshTemporaryDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.ISshService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/14
 */
@Api(tags = "ssh连接")
@RestController
@RequestMapping("/ssh")
public class SshController {

    @Resource
    private ISshService sshService;

    @ApiOperation("新增临时ssh连接记录")
    @PostMapping("/addTemporarySsh")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result addTemporarySsh(@RequestBody @Valid SshTemporaryDto sshTemporaryDto,
                                    HttpServletRequest request,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return sshService.addTemporarySsh(sshTemporaryDto,request);
    }

    @ApiOperation("查询临时连接记录")
    @GetMapping("/getByToken")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getByToken(HttpServletRequest request){
        return sshService.getByToken(request);
    }
}
