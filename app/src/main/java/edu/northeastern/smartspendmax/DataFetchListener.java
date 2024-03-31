package edu.northeastern.smartspendmax;

import java.util.List;

public interface DataFetchListener {
    void onDataFetched(List<CategoryInsight> insights);
}
