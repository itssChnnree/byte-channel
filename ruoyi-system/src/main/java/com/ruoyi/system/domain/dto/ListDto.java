package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [批量删除id集合]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListDto {


    private List<String> ids;
}
