#ifndef _SIM_DATA_H
#define _SIM_DATA_H

#include <assert.h>
#include <algorithm>
#include <unordered_map>
#include <unordered_set>
#include "tquant_api.h"
#include "stralet.h"

using namespace std;
using namespace tquant::api;
using namespace tquant::stra;


class SimStraletContext;

struct TickCache {
    int64_t pos;
    shared_ptr<vector<MarketQuote>> ticks;
};

struct BarTickCache {
    int64_t pos;
    shared_ptr<vector<Bar>> bars;
};

struct DailyBarTickCache {
    int pos;
    shared_ptr<vector<DailyBar>> bars;
};

class SimDataApi : public DataApi {
    friend SimStraletContext;
public:

    SimDataApi(SimStraletContext* ctx, DataApi* dapi)
        : m_ctx(ctx)
        , m_dapi(dapi)
    {
    }

    virtual CallResult<vector<MarketQuote>> tick(const char* code, int trading_day) override;
    virtual CallResult<vector<Bar>>         bar(const char* code, const char* cycle, int trading_day, bool align) override;
    virtual CallResult<vector<DailyBar>>    daily_bar(const char* code, const char* price_adj, bool align) override;
    virtual CallResult<MarketQuote>         quote(const char* code) override;
    virtual CallResult<vector<string>>      subscribe(const vector<string>& codes) override;
    virtual CallResult<vector<string>>      unsubscribe(const vector<string>& codes) override;

    virtual void set_callback(DataApi_Callback* callback) override;

    void calc_nex_time(DateTime* dt);

    shared_ptr<MarketQuote> next_quote(const string& code);
    shared_ptr<Bar> next_bar(const string & code);
    const Bar* last_bar(const string & code);

    DataApi* dapi() { return m_dapi; }

    void move_to(int trading_day);

    const unordered_set<string>& sub_codes() { return m_codes; }
private:
    SimStraletContext* m_ctx;
    DataApi* m_dapi;
    unordered_map<string, TickCache>    m_tick_caches;
    unordered_map<string, BarTickCache> m_bar_caches;
    unordered_set<string> m_codes;
};

#endif