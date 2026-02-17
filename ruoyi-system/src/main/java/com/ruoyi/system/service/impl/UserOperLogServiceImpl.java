package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.domain.base.LogEntry;
import com.ruoyi.system.domain.dto.log.*;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IUserOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.github.benmanes.caffeine.cache.Cache;
import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * [用户操作日志]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2024/2/21
 */
@Service("UserOperLogServiceImpl")
@Slf4j
public class UserOperLogServiceImpl implements IUserOperLogService {


    @Resource
    private RestHighLevelClient restHighLevelClient;

    private final String USER_LOG_OPER_INDEX = "user_oper_log";

    @Resource(name = "traceIdIndex")
    private Cache<String, String> cache;
    /**
     * [通过es查询用户操作日志]
     *
     * @param sysUserOperLogDto 查询入参
     * @return com.edu.educenter.core.common.entity.Result 查询结果
     * @author 陈湘岳 2024/2/23
     **/
    @Override
    public Result getDateHistogram(SysUserOperLogDto sysUserOperLogDto) {

        //构建查询及索引
        SearchRequest searchRequest = new SearchRequest(sysUserOperLogDto.getIndex());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool查询
        searchSourceBuilder.query(getBoolQueryBuilder(sysUserOperLogDto));
        //排序
        sortBuild(searchSourceBuilder,sysUserOperLogDto.getSortField(),sysUserOperLogDto.getSortType());
        searchSourceBuilder.size(500);
        //开启高亮
        highLightBuilder(searchSourceBuilder, sysUserOperLogDto.getOpenHighLight());
        //分页
        pageBuilder(searchSourceBuilder,sysUserOperLogDto.getPage(), sysUserOperLogDto.getSize());
        //创建搜索请求
        searchRequest.source(searchSourceBuilder);
        SearchResponse search=null;
        try {
             search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        }
        catch (ElasticsearchStatusException e) {
            log.error("es查询数据失败"+e);
            if (e.getDetailedMessage().contains("too_many_buckets_exception")){
                throw new RuntimeException("查询粒度过细或时间范围过大，请重试");
            } else{
                throw new RuntimeException("查询失败，请联系管理员");
            }
        }catch (IOException e){
            log.error("es查询数据失败"+e);
            throw new RuntimeException("es查询数据失败");
        }
        AggregationDTO aggregationDTO = new AggregationDTO();
        //为空则查询失败
        if (ObjectUtils.isEmpty(search)){
            return Result.fail("查询失败，请稍后再试");
        }else {
            List<Object> hits = getHits(search.getHits());
            aggregationDTO.setHit(hits);
            aggregationDTO.setTotal(search.getHits().getTotalHits().value);
            aggregationDTO.setPage(sysUserOperLogDto.getPage());
            aggregationDTO.setSize(sysUserOperLogDto.getSize());
        }
        return Result.success(aggregationDTO);
    }

    /**
     * [日志保存]
     *
     * @param logEntry 日志
     * @return void
     * @author 陈湘岳 2026/2/3
     **/
    @Override
    public void save(LogEntry logEntry) {
        String type ="_doc";
        // 将对象转为json
        String data = JSON.toJSONString(logEntry);
        String index = cache.get(logEntry.getTraceId(), (key) -> {
            String timeNow = DateUtils.dateTimeNow("yyyy");
            return USER_LOG_OPER_INDEX+"_"+timeNow;
        });

        IndexRequest indexRequest = new IndexRequest(index,type)
                .id(TraceIdContext.generateTraceId()).source(data, XContentType.JSON);
        // 执行增加文档
        IndexResponse response =null;
        try {
            response=restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("调用es服务失败"+e);
            throw new RuntimeException("调用es服务失败");
        }
    }


    //是否开启高亮
    private void highLightBuilder(SearchSourceBuilder searchSourceBuilder,int openHighLight){
        if (openHighLight==1){
            HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false).preTags("<span class=\"highlight\">").postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
    }

    private void pageBuilder(SearchSourceBuilder searchSourceBuilder,int pageNum,int pageSize){
        if (pageNum<1){
            pageNum=1;
        }
        if (pageSize<=0){
            pageSize=10;
        }
        searchSourceBuilder.from((pageNum - 1)*pageSize).size(pageSize);

    }

    //获取es中的数据
    public List<Object> getHits(SearchHits hits){
        List<Object> objectList = new ArrayList<>();
        if (ObjectUtils.isEmpty(hits)){
            return objectList;
        }
        //循环数据放入map并返回
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //高亮词语替换
            substitute(sourceAsMap,highlightFields);
            objectList.add(sourceAsMap);
        }
        return objectList;
    }


    //将高亮替换掉非高亮词语
    private void substitute(Map<String, Object> map,Map<String, HighlightField> highlightFields){
        if (MapUtils.isNotEmpty(highlightFields)&&MapUtils.isNotEmpty(map)){
            Set<String> keys = highlightFields.keySet();
            keys.stream().forEach(s->{
                if (!ObjectUtils.isEmpty(map.get(s))){
                    HighlightField highlightField = highlightFields.get(s);
                    Text[] fragments = highlightField.getFragments();
                    String hightStr = null;
                    if (fragments != null) {
                        for (Text str : fragments) {
                            hightStr = str.string();
                        }
                    }
                    map.put(s,hightStr);
                }
            });
        }
    }



    private List<AggregationsTimeDTO> getAggs(Aggregation aggregations){
        List<AggregationsTimeDTO> aggs = new ArrayList<>();
        if (ObjectUtils.isEmpty(aggregations)){
            return aggs;
        }
        List<? extends Histogram.Bucket> buckets = ((Histogram) aggregations).getBuckets();
        for (Histogram.Bucket bucket : buckets) {
            String key = bucket.getKey().toString();
            String time = DateUtils.convertToYYYYMMDDhhmmss(key);
            long docCount = bucket.getDocCount();
            aggs.add(new AggregationsTimeDTO( time,docCount));
        }
        return aggs;
    }



    //构建bool查询
    private BoolQueryBuilder getBoolQueryBuilder(SysUserOperLogDto sysUserOperLogDto){
        //构建boolFilter
        BoolQueryBuilder boolFilter = QueryBuilders.boolQuery();
        //构建时间查询
        timeFilterBuild(boolFilter,sysUserOperLogDto);
        //构建查询条件
        queryBuild(boolFilter,sysUserOperLogDto);
        //构建过滤器
        filterBuilds(boolFilter,sysUserOperLogDto);
        return boolFilter;
    }

    private void filterBuilds(BoolQueryBuilder boolFilter,SysUserOperLogDto sysUserOperLogDto){
        List<OperFilterDTO> filterList = sysUserOperLogDto.getFilterList();
        if(!CollectionUtils.isEmpty(filterList)){
            for (OperFilterDTO operFilterDTO:filterList){
                filterBuild(boolFilter,operFilterDTO);
            }
        }
    }


    private void filterBuild(BoolQueryBuilder boolFilter, OperFilterDTO operFilterDTO){
            switch (operFilterDTO.getOperator()){
                case 2:isNotFilterBuild(boolFilter,operFilterDTO);break;
                case 3:isOneOfFilterBuild(boolFilter,operFilterDTO);break;
                case 4:isNotOneOfFilterBuild(boolFilter,operFilterDTO);break;
                case 5:existsFilterBuild(boolFilter,operFilterDTO);break;
                case 6:doesNotExistsFilterBuild(boolFilter,operFilterDTO);break;
                case 1: default:isFilterBuild(boolFilter,operFilterDTO);break;
            }
    }


    //构建存在过滤条件
    private void isFilterBuild(BoolQueryBuilder boolFilter,OperFilterDTO operFilterDTO){
        if (!StringUtils.isEmpty(operFilterDTO.getField())&&!CollectionUtils.isEmpty(operFilterDTO.getValue())){
            MatchPhraseQueryBuilder matchPhraseQueryBuilder
                    = new MatchPhraseQueryBuilder(operFilterDTO.getField(), operFilterDTO.getValue().get(0));
            boolFilter.filter(matchPhraseQueryBuilder);
        }
    }


    //构建不存在过滤条件
    private void isNotFilterBuild(BoolQueryBuilder boolFilter,OperFilterDTO operFilterDTO){
        if (!StringUtils.isEmpty(operFilterDTO.getField())&&!CollectionUtils.isEmpty(operFilterDTO.getValue())){
            MatchPhraseQueryBuilder mustNotMatchPhraseQueryBuilder
                    = new MatchPhraseQueryBuilder(operFilterDTO.getField(), operFilterDTO.getValue().get(0));
            boolFilter.mustNot(mustNotMatchPhraseQueryBuilder);
        }
    }

    //构建可能存在过滤条件
    private void isOneOfFilterBuild(BoolQueryBuilder boolFilter,OperFilterDTO operFilterDTO){
        if (!StringUtils.isEmpty(operFilterDTO.getField())&&!CollectionUtils.isEmpty(operFilterDTO.getValue())){
            BoolQueryBuilder shouldClauseBuilder = QueryBuilders.boolQuery();
            for (String s: operFilterDTO.getValue()){
                MatchPhraseQueryBuilder matchPhraseQueryBuilder
                        = new MatchPhraseQueryBuilder(operFilterDTO.getField(), s);
                //创建should子句
                shouldClauseBuilder.should(matchPhraseQueryBuilder);

            }
            boolFilter.filter(shouldClauseBuilder);
        }
    }

    //构建可能不存在过滤条件
    private void isNotOneOfFilterBuild(BoolQueryBuilder boolFilter,OperFilterDTO operFilterDTO){
        if (!StringUtils.isEmpty(operFilterDTO.getField())&&!CollectionUtils.isEmpty(operFilterDTO.getValue())) {
            BoolQueryBuilder shouldClauseBuilder = QueryBuilders.boolQuery();
            for (String s : operFilterDTO.getValue()) {
                MatchPhraseQueryBuilder matchPhraseQueryBuilder = new MatchPhraseQueryBuilder(operFilterDTO.getField(), s);
                //创建should子句
                shouldClauseBuilder.should(matchPhraseQueryBuilder);
            }
            boolFilter.mustNot(shouldClauseBuilder);
        }
    }

    //构建字段存在过滤条件
    private void existsFilterBuild(BoolQueryBuilder boolFilter,OperFilterDTO operFilterDTO){
        if (!StringUtils.isEmpty(operFilterDTO.getField())) {
            // 创建exists查询构建器
            ExistsQueryBuilder existsQueryBuilder = new ExistsQueryBuilder(operFilterDTO.getField());
            boolFilter.filter(existsQueryBuilder);
        }
    }

    //构建字段不存在过滤条件
    private void doesNotExistsFilterBuild(BoolQueryBuilder boolFilter,OperFilterDTO operFilterDTO){
        if (!StringUtils.isEmpty(operFilterDTO.getField())) {
            ExistsQueryBuilder notExistsQueryBuilder = new ExistsQueryBuilder(operFilterDTO.getField());
            boolFilter.mustNot(notExistsQueryBuilder);
        }
    }




    private void timeFilterBuild(BoolQueryBuilder boolFilter,SysUserOperLogDto sysUserOperLogDto){
        //构建起始时间
        if(!StringUtils.isEmpty(sysUserOperLogDto.getStartTime())){
            String beginTime = buildTimeRange(sysUserOperLogDto.getStartTime());
            boolFilter.filter(QueryBuilders.rangeQuery("timestampMs").gte(beginTime));
        }
        //构建结束时间
        if(!StringUtils.isEmpty(sysUserOperLogDto.getEndTime())){
            String endTime = buildTimeRange(sysUserOperLogDto.getEndTime());
            boolFilter.filter(QueryBuilders.rangeQuery("timestampMs").lte(endTime));
        }
    }


    private void queryBuild(BoolQueryBuilder boolFilter,SysUserOperLogDto sysUserOperLogDto){
        if(!StringUtils.isEmpty(sysUserOperLogDto.getQuery())){
            boolFilter.must(QueryBuilders.queryStringQuery(sysUserOperLogDto.getQuery()));
        }
    }


    private void sortBuild(SearchSourceBuilder searchSourceBuilder,String sortField,int sortType){
        FieldSortBuilder sortByScore = new FieldSortBuilder("_score").order(SortOrder.DESC);
        searchSourceBuilder.sort(sortByScore);
        if (StrUtil.isNotBlank(sortField)){
            SortOrder  sortOrder = sortType == 1 ? SortOrder.ASC : SortOrder.DESC;
            FieldSortBuilder sort = new FieldSortBuilder(sortField).order(sortOrder);
            searchSourceBuilder.sort(sort);
        }else {
            FieldSortBuilder sortByTime = new FieldSortBuilder("timestampMs").order(SortOrder.DESC);
            searchSourceBuilder.sort(sortByTime);
        }
    }


    /**
     * 判断字符串是否只包含数字
     * @param str 待检查的字符串
     * @return 如果字符串只包含数字，返回true；否则返回false
     */
    public static boolean isNumeric(String str) {
        // 正则表达式，只匹配数字
        String regex = "^\\d+$";
        return str != null && str.matches(regex);
    }

    private String buildTimeRange(String time){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }


    private DateHistogramInterval getTimeType(int type){

        switch (type){
            case 1:return DateHistogramInterval.seconds(1);
            case 2:return DateHistogramInterval.minutes(1);
            case 3:return DateHistogramInterval.hours(1);
            case 4:return DateHistogramInterval.days(1);
            case 5:return DateHistogramInterval.days(7);
            case 6:return DateHistogramInterval.days(30);
            case 7:return  DateHistogramInterval.days(90);
            case 8:return  DateHistogramInterval.days(365);
            case 0:
            default:return DateHistogramInterval.seconds(15);

        }
    }


}
