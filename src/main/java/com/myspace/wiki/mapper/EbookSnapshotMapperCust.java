package com.myspace.wiki.mapper;

import com.myspace.wiki.response.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {

    public void genSnapshot();
    List<StatisticResp> getStatistic();
    List<StatisticResp> get30Statistic();
}
