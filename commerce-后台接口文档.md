## Commerce 
### API
#### 说明
```
    API调用方法：
       http://host:port/xxx
    举例: 
        API为/api/ad/black
        host为localhost
        port为8080

        则该API的URL为localhost:8080/api/ad/black
```
#### 本项目基本信息
###### host
    39.108.234.110
###### port
    8989
### API 详情
    1. GET /api/ad/black
        获取ad_blacklist表中的全部数据
        请求方法为GET
        
    2. GET /api/ad/click/trend
        获取ad_click_trend表中的全部数据
        请求方法为GET
        
    3. GET /api/ad/province/top3
        获取ad_province_top3表中的全部数据
        请求方法为GET
     
    4. GET /api/ad/stat
        获取ad_stat表中的全部数据
        请求方法为GET
    
    5. GET /api/ad/user/click/count
        获取ad_user_click_count表中的全部数据
        请求方法为GET
    
    6. GET /api/area/top3/product
        获取area_top3_product表中的全部数据
        请求方法为GET
    
    7. GET /api/page/split/convert/rate
        获取page_split_convert表中的全部数据
        请求方法为GET
    
    8. GET /api/session/aggr/stat
        获取session_aggr_stat表中的全部数据
        请求方法为GET
    
    9. GET /api/session/detail
        获取session_detail表中的全部数据
    
    10. GET /api/session/random/extract
        获取session_random_extract表中的全部数据
        请求方法为GET
      
    11. GET /api/top10/category
        获取top10_category表中的全部数据
        请求方法为GET
    
    12. GET /api/top10/session
        获取top10_session表中的全部数据
        请求方法为GET

##### 例子
    浏览器输入39.108.234.110:8989/api/ad/black
    即可获取到ad_blacklist中的全部数据
##### Attention
    JQuery中$.get(xxx)表示发送的是GET请求，这里提供的API都为GET请求。
        
