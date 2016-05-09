<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    <title>Demo</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href="/static/js/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="/static/js/bootstrap/css/bootstrap-theme.min.css">
    <!-- menu css -->
    <link rel="stylesheet" href="/static/js/menu/css/font-awesome.min.css">
    <link rel="stylesheet" href="/static/js/menu/css/metismenu.min.css">
    <link rel="stylesheet" href="/static/js/menu/css/demo.css">
    <link rel="stylesheet" href="/static/js/menu/css/prism.min.css">
    <link rel="stylesheet" type="text/css" href="/static/js/menu/css/default.css">

    <script src="/static/js/jquery/jquery.min.js"></script>
    <script src="/static/js/bootstrap/js/bootstrap.min.js"></script>

    <script src="/static/js/echarts/echarts-all.js"></script>
</head>

<body>
<div class="htmleaf-container">
    <header class="htmleaf-header">
        <h1>下拉列表插件</h1>
    </header>
    <div class="htmleaf-content bgcolor-8">
        <div class="clearfix">
            <aside class="sidebar">
                <nav class="sidebar-nav">
                    <ul id="menu">
                        <c:if test="${menu==null || fn:length(menu) == 0}">
                            <li class="active">
                                <a href="<c:url value="/api/app/index" />">Menu<span class="glyphicon arrow"></span></a>
                                <ul>
                                    <li><a href="#">no data...</a></li>
                                </ul>
                            </li>
                        </c:if>
                        <c:forEach items="${menu}" var="data">
                            <li>
                                <a href="<c:url value="/api/app/index" />">${data.key}<span class="glyphicon arrow"></span></a>
                                <ul>
                                    <c:forEach items="${data.value}" var="item" varStatus="status">
                                        <li><a href="javascript:init(${data.key}, ${item});">${item} callers</a></li>
                                        <li><a href="/api/app/graph2/${data.key}/${item}">${item} codes</a></li>
                                        <li><a href="/api/app/graph3/${data.key}/${item}">${item} costs</a></li>
                                        <li><a href="/api/app/graph4/${data.key}/${item}">${item} requests</a></li>
                                        <hr style="height:1px;border:none;border-top:1px dashed #0066CC;" />
                                    </c:forEach>
                                </ul>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </aside>
            <section class="content">
                <div class="col-xs-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            调用者峰值
                            <span class="pull-right"><span class="fa fa-code"></span></span>
                        </div>
                        <div class="panel-body" id="main" style="height:400px">
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>

<script src="/static/js/menu/js/metismenu.js"></script>

<script>
    /**
     * 获取用户IP
     * @returns {Array}
     */
    function getLegend(ds) {
        //获取用户IP
        var caller = [];
        $.each(ds, function(i, v) {
            $.each(v.callers, function(k, v) {
                if(!caller.indexOf(k)){
                    caller.push(k);
                }
            })
        })
        console.log(caller)
        return caller;
    }

    /**
     * 获取时间
     * @returns {Array}
     */
    function getXAxis(ds) {
        //时间
        var time = [];
        $.each(ds, function(i, v) {
            if(!time.indexOf(v.time)){
                time.push(v.time);
            }
        })
        console.log(time)
        return time;
    }
    
    function getSeries(ds, caller, time) {
        var series = [];
        $.each(caller, function(i, v) {
            var cal = {};
            cal['name'] = v;
            cal['type'] = 'bar';
            cal['data'] = [];
            $.each(time, function(i1, v1) {
                var cnt = 0;
                $.each(ds, function(i2, v2) {
                    $.each(v2.callers, function(k3, v3) {
                        if(!caller.indexOf(k3)){
                            if(k3 == v){
                                cnt = v3;
                            }
                        }
                    })
                })
                cal['data'].push(cnt);
            });
            series.push(cal);
        });
        return series;
    }
    
    function initEcharts(caller, time, series) {
        // 基于准备好的dom，初始化echarts图表
        var myChart = echarts.init(document.getElementById('main'));

        option = {
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend: {
                data: caller
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    data : time
                }
            ],
            yAxis : [
                {
                    type : 'value'
                }
            ],
            series : series
        };

        // 为echarts对象加载数据
        myChart.setOption(option);
    }
    
    function init(serverIp, serviceId) {
        $.ajax( {
            url:'/api/app/graph1/',
            data:{
                serverIp : serverIp,
                serviceId : serviceId
            },
            type:'post',
            cache:false,
            dataType:'json',
            success:function(data) {
                if(data.code =="200" ){
                    var ds = data.data;
                    var caller = getLegend(ds);
                    var time = getXAxis(ds);
                    var series = getSeries(ds, caller, time);
                    initEcharts(caller, time, series)
                }else{
                    alert("无数据！");
                }
            },
            error : function() {
                alert("异常！");
            }
        });
    }

    $(function () {
        $('#menu').metisMenu({
            doubleTapToGo: true
        });
    });

</script>

<script src="/static/js/menu/js/prism.min.js"></script>
</body>
</html>