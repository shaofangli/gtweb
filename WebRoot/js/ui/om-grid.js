/*
 * $Id: om-grid.js,v 1.124 2012/03/16 06:19:29 chentianzhen Exp $
 * operamasks-ui omGrid @VERSION
 *
 * Copyright 2011, AUTHORS.txt (http://ui.operamasks.org/about)
 * Dual licensed under the MIT or LGPL Version 2 licenses.
 * http://ui.operamasks.org/license
 *
 * http://ui.operamasks.org/docs/
 * 
 * Depends:
 *  om-core.js
 *  om-mouse.js
 *  om-resizable.js
 */
 
/**
     * @name omGrid
     * @class 表格组件。类似于html中的table，支持后台数据源、分页、自动列宽、单选/多选、行样式、自定义列渲染等功能。<br/><br/>
     * <b>特点：</b><br/>
     * <ol>
     *      <li>使用远程数据源</li>
     *      <li>支持分页展现</li>
     *      <li>自动添加行号</li>
     *      <li>允许某列的宽度自动扩充（该列宽度等于表格总宽度送去其它列宽度之和）</li>
     *      <li>允许所有列自动缩放（自动缩放各列的宽度，使得它适应表格的总宽度）</li>
     *      <li>可以定制隔行样式，也可以根据记录的不同使用不同的行样式</li>
     *      <li>可以定制各列的显示效果</li>
     *      <li>可以设置表头和表体自动换行</li>
     *      <li>可以改变列宽</li>
     *      <li>提供丰富的事件</li>
     * </ol>
     * 
     * <b>示例：</b><br/>
     * <pre>
     * &lt;script type="text/javascript" >
     *  $(document).ready(function() {
     *      $('#mytable').omGrid({
     *          height : 250,
     *          width : 600,
     *          limit : 8, //分页显示，每页显示8条
     *          singleSelect : false, //出现checkbox列，可以选择同时多行记录
     *          colModel : [    {header:'编号',name:'id', width:100, align : 'center'},
     *                          {header:'地区',name:'city', width:250, align : 'left',wrap:true},
     *                          {header:'地址',name:'address', width:'autoExpand',renderer:function(value){ return '&lt;b>'+value+'&lt/b>'; }}
     *          ],
     *          dataSource : 'griddata.json' //后台取数的URL
     *      });
     *  });
     * &lt;/script>
     * 
     * &lt;table id="mytable"/>
     * </pre>
     * 
     * colModel配置项是列模型，它定义了表格与具体数据无关的展现。它是一个JSON数组，其中数组中每一个item（是一个JSON对象）代表表格中的一列。可以使用以下属性：<br/>
     * <ol>
     *      <li>header:String类型，表示表格的表头文字。无默认值。</li>
     *      <li>width:Number类型或'autoExpand'，如果是数字200表示该列宽度是200px。如果是'autoExpand'表示该列的宽度是表格总宽度减去其它各列宽度之和。默认值是60。</li>
     *      <li>align:'left'或'right'或'center'，表示表格的表头和表体文字的对齐方式。默认值是'left'。</li>
     *      <li>name:String类型，说明表体这一列要显示后台返回的数据记录（JSON对象）中的哪个属性的值，如name:'sex'表示这一列显示JSON对象的sex属性的值。无默认值。</li>
     *      <li>wrap:Boolean类型，说明这一列的表头和表体当内容太多时是否允许自动换行显示，如wrap:true表示这一列的表头和表体在显示时自动换行（比如该列宽度是100px但是表体某td内容宽度是120px时则该td的内容自动换行显示）。默认值是false。</li>
     *      <li>renderer:Function类型，允许在显示该列的值是进行客户端处理。无默认值（不做转换直接输出，比如id是'name'的列将会直接把JSON行数据中的name属性的值显示在表格的td中）。有时可能需要进行一些处理，比如：<br/>
     *          <ul>
     *              <li>该td中的字体要加粗可以使用
     *                  <pre>
     *      renderer:function(value){
     *          return '&lt;b>'+value+'&lt;/b>';
     *      }
     *                  </pre>
     *              </li>
     *              <li>比如后台返回的数据中sex是0或1，页面中要转换为中文'男'和'女'可以使用
     *                  <pre>
     *      renderer:function(value){
     *          return value==0?'男':'女';
     *      }
     *                  </pre>
     *              </li>
     *              <li>比如一个后台返回的数据中score是分数，现要求100分输出为绿色的'满分'小于60分时输出红色的'不及格'其它的不变，可以使用
     *                  <pre>
     *      renderer:function(value){ 
     *          if(value==100){
     *              return value==100?'&lt;font color="green">满分&lt;/font>';
     *          }else if(value&lt;60){
     *              return '&lt;font color="red">不及格&lt;/font>';
     *          }else{
     *              return value;
     *          }
     *      }
     *                  </pre>
     *              </li>
     *          <ul>
     *      </li>
     * </ol>
     * 
     * 后台返回的数据格式如下（可以不包含空格换行等格式内容，大括号内的各属性顺序可任意交换）：<br/>
     * <pre>
     * {"total":126, "rows":
     *     [
     *         {"address":"CZ88.NET ","city":"IANA保留地址","id":"1"},
     *         {"address":"CZ88.NET ","city":"澳大利亚","id":"2"},
     *         {"address":"电信","city":"福建省","id":"3"},
     *         {"address":"CZ88.NET ","city":"澳大利亚","id":"4"},
     *         {"address":"CZ88.NET ","city":"泰国","id":"5"},
     *         {"address":"CZ88.NET ","city":"日本","id":"6"},
     *         {"address":"电信","city":"广东省","id":"7"},
     *         {"address":"CZ88.NET ","city":"日本","id":"8"}
     *     ]
     * }
     * </pre>
     * 
     * <b>其它特殊说明：</b><br/>
     * 单击一行时有可能会触发onRowSelect、onRowDeselect、onRowClick这些事件中一个一个或多个。具体结果是这样的：
     * <ol>
     *     <li>单选（一次只能选择一行）表格：①如果该行还未被选中，先触发其它已选择的行的onRowDeselect事件监听再触发该行的onRowSelect事件监听②触发该行的onRowClick事件监听。</li>
     *     <li>多选（一次可以选择多行）表格：①如果该行还未被选中，先触发该行的onRowSelect事件监听，如果该行已经选中，则先触发该行的onRowDeselect事件监听②触发该行的onRowClick事件监听。</li>
     * </ol>
     * 
     * @constructor
     * @description 构造函数. 
     * @param p 标准config对象：{}
     */
;(function($) {
    $.omWidget('om.omGrid', {
        options:/** @lends omGrid#*/{
            //外观
            /**
             * 表格高度，单位为px。
             * @default 462
             * @type Number
             * @example
             * $('.selector').omGrid({height : 300});
             */
            height:462,
            /**
             * 表格宽度，单位为px。
             * @type Number
             * @default '100%'
             * @example
             * $('.selector').omGrid({width : 600});
             */
            width:'100%',
            /**
             * 列数据模型。每一个元素都是一个对象字面量，定义该列的各个属性，这些属性包括:<br/>
             * header : 表头文字。<br/>
             * name : 与数据模型对应的字段。<br/>
             * align : 列文字对齐方式，可以为'left'、'center'、'right'之中的一个。<br/>
             * renderer : 列的渲染函数，接受2个参数，v表示当前值，row表示当前行号。<br/>
             * width : 列的宽度，取值为Number或者'autoExpand'。注意只能有一个列被设置为'autoExpand'属性。<br/>
             * wrap : 是否自动换行，取值为true或者false。<br/>
             * @type Array[JSON]
             * @default false
             * @example
             * 
             * $(".selector").omGrid({
             *      colModel : [ {
             *              header : '地区',          //表头文字
             *              name : 'city',          //与数据模型对应的字段
             *              width : 120,            //列宽,可设置具体数字，也可设置为'autoExpand'，表示自动扩展
             *              align : 'left',         //列文字对齐
             *              renderer : function(v, row) {   //列渲染函数，接受2个参数，v表示当前值，row表示当前行号
             *                  return '&lt;b>'+v+'&lt;/b>';  //地区这一列的文字加粗显示
             *              }
             *          }, {
             *              header : '地址',
             *              name : 'address',
             *              align : 'left',
             *              width : 'autoExpand'
             *          } 
             *      ]
             * });
             */
            colModel:false,
            /**
             * 是否自动拉伸各列以适应表格的宽度（比如共2列第一列宽度100第二列宽度200，则当表格总宽度是600px时第一列自动会变成200px第二列宽度会自动变成400px，而如果表格总宽度是210px时第一列自动会变成70px第二列宽度会自动变成140px）。<b>注意：只有所有列的宽度都不是'autoExpand'时该属性才会起作用。</b>
             * @default false
             * @type Boolean
             * @example
             * $('.selector').omGrid({autoFit : true});
             */
            autoFit:false,
            /**
             * 是否在最左边显示序号列。
             * @default true
             * @type Boolean
             * @example
             * $('.selector').omGrid({showIndex : false});
             */
            showIndex:true,
            //数据源
            /**
             * ajax取数方式对应的url地址。
             * @type String
             * @default 无
             * @example
             * //下面的示例设置的url，表示将从griddata.json这个地址取数，同时附带有start和limit两个请求参数。
             * //该文件必须返回一段具有特定格式（格式可参考文档的“预览”页签的说明）的JSON数据，omGrid拿到该数据即可用来填充表格。
             * $('.selector').omGrid({url:'griddata.json'});
             */
            dataSource:false,
             /**
             * ajax取数时附加到请求的额外参数。<b>注意：这里JSON的value值只能使用普通值，比如可以设置为{key1:1,key2:'2',key3:0.2,key4:true,key5:undefined}这样，但是不可以设置为{key1:[]}或{key2:{a:1,b:2}}，因为[]和{a:1,b:2}都不是普通值</b>
             * @type JSON
             * @default {}
             * @example
             * //下面的示例在Ajax取数时将从griddata.json这个地址取数，同时附带有start、limit、googType、localtion这4个请求参数。
             * //真正的URL地址可能是griddata.json?start=0&limit=10&goodType=1&location=beijing
             * $('.selector').omGrid({url:'griddata.json',extraData:{googType:1,localtion:'beijing'} });
             */
            extraData:{},
            /**
             * 使用GET请求还是POST请求来取数据，取值为：'POST'或'GET'。
             * @type String
             * @default 'GET'
             * @example
             * $('.selector').omGrid({method : 'POST'});
             */
            method:'GET',
            /**
             * 正在取数时显示在分页条上的提示。
             * @name omGrid#loadingMsg
             * @type String
             * @default '正在加载数据，请稍候...'
             * @example
             * $('.selector').omGrid({loadingMsg : '取数中...'});
             */
            //loadingMsg:$.om.lang.omGrid.loadingMsg,
            /**
             * 取数完成后但是后台没有返回任何数据时显示在分页条上的提示。
             * @name omGrid#emptyMsg
             * @type String
             * @default '没有数据'
             * @example
             * $('.selector').omGrid({emptyMsg : 'No data!'});
             */
            //emptyMsg:$.om.lang.omGrid.emptyMsg,
            /**
             * 取数发生错误时显示在分页条上的提示。
             * @name omGrid#errorMsg
             * @type String
             * @default '取数出错'
             * @example
             * $('.selector').omGrid({emptyMsg : '应用异常，请与网站管理员联系!'});
             */
            //errorMsg:$.om.lang.omGrid.errorMsg,
            /**
             * 取数成功后的预处理，可以在取数成功后开始显示数据前对后台返回的数据进行一次预处理。<b>注意：此方法一定要返回一个值</b>。
             * @type Function
             * @default 无
             * @example
             * //将后台返回的数据中所有记录的id属性改名成name属性，并将sex中的0/1分别转换为'男'或'女'。
             * //如后台返回{"total":35,"rows":[{id:1,sex:0,password:'abc'},{id:2,sex:1,password:'def'}]}
             * //转换后结果为{"total":35,"rows":[{name:1,sex:'男',password:'abc'},{name:2,sex:'女',password:'def'}]}
             * $('.selector').omGrid({preProcess : function(data){
             *          var temp;
             *          for(var i=0,len=data.rows.length;i&lt;len;i++){
             *              temp=data.rows[i];
             *              temp.name=temp.id;
             *              temp.id=undefined;
             *              temp.sex= temp.sex==0?'男':'女';
             *          }
             *          return data;
             *      }
             * });
             */
            preProcess:false,
            //分页
            /**
             * 每页数据条数，比如每页要显示10条则设成10。<b>注意：如果设成0或负数则不分页</b>。此属性仅用于取数不用于显示（即如果limit设成10，取数时告诉后台要10条数据，如果后台非要返回15条数据，则页面会显示出15条而不是10条数据）。
             * @type Number
             * @default 15
             * @example
             * $('.selector').omGrid({limit : 15});
             */
            limit:15,
            /**
             * 显示在分页条上“上一页”和“下一页”按钮之间的文字。在显示时其中的{totalPage}会被替换为总页数，{index}会被替换为一个输入框（默认显示当前的页号，用户可以输入任意数字然后回车来跳转到指定的页）。
             * @name omGrid#pageText
             * @type String
             * @default '第{index}页，共{totalPage}页'
             * @example
             * $('.selector').omGrid({pageText : '共{totalPage}页，转到{index}页'});
             */
            //pageText:$.om.lang.omGrid.pageText,
            /**
             * 显示在分页条上的统计文字。在显示时其中的{total}会被替换为总记录数，{from}和{to}会被替换为当前显示的起止行号。比如可能会显示成'共125条数据，显示21-30条'。
             * @name omGrid#pageStat
             * @type String
             * @default '共{total}条数据，显示{from}-{to}条'
             * @example
             * $('.selector').omGrid({pageStat : '总共有{total}条记录，当前正在显示第{from}行至第{to}行'});
             */
            //pageStat:$.om.lang.omGrid.pageStat,
            //行显示
            /**
             * 行样式，默认显示成斑马纹（奇偶行背景不一样）。当然用户也可以定义成3行一循环或5行一循环。也可以定义成一个Function来根据行数据不同显示成不同的样式（比如一个显示学生成绩的表格中把不及格的记录整行显示成红色背景，满分的记录整行显示成绿色背景）。
             * @type Array或Function
             * @default ['oddRow','evenRow']
             * @example
             * 
             * //示例1：结果表格中第1/4/7/10...行的tr会加上样式class1；
             * //第2/5/8/11...行的tr会加上样式class2；
             * //第3/6/9/12...行的tr会加上样式class3
             * $('.selector').omGrid({rowClasses : ['class1','class2','class2']});
             * 
             * //示例2：满分的行加上样式fullMarks，不及格的行加上样式flunk，其它行使用默认样式。
             * $('.selector').omGrid({rowClasses : function(rowIndex,rowData){
             *          if(rowData.score==100){
             *              reuturn 'fullMarks';
             *          }else if(rowData.score<60){
             *              return 'flunk';
             *          }
             *      }
             * });
             */
            rowClasses:['oddRow','evenRow'],
            //行选择
            /**
             * 是否只能单选（一次只能选择一条记录，选择第二条时第一条会自动取消选择）。若设置为false表示可以多选（选择其它行时原来已经选择的将继续保持选择状态）。<b>注意：设成true时将不会出现checkbox列，设成false则将自动出现checkbox列</b>。
             * @type Boolean
             * @default true
             * @example
             * $('.selector').omGrid({singleSelect : false});
             */
            singleSelect:true,
            
            /**
             * 设置组件的标题
             * @type String
             * @default ''
             * @example
             * $('.selector').omGrid({title : 'Data Grid'});
             */
            title: '',
            
            //event
            /**
             * 选择一行记录后执行的方法。
             * @event
             * @type Function
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @param event jQuery.Event对象。
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onRowSelect:function(rowIndex,rowData,event){
             *          alert('the '+rowIndex+'th row has been selected!');
             *      }
             *  });
             */
            onRowSelect:function(rowIndex,rowData,event){},
            /**
             * 取消一行记录的选择后执行的方法。
             * @event
             * @type Function
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onRowDeselect:function(rowIndex,rowData,event){
             *          alert('the '+rowIndex+'th row has been deselected!');
             *      }
             *  });
             */
            onRowDeselect:function(rowIndex,rowData,event){},
            /**
             * 单击一行记录后执行的方法。
             * @event
             * @type Function
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onRowClick:function(rowIndex,rowData,event){
             *          alert('the '+rowIndex+'th row has been clicked!city='+rowData.city);
             *      }
             *  });
             */
            onRowClick:function(rowIndex,rowData,event){},
            /**
             * 双击一行记录后执行的方法。
             * @event
             * @type Function
             * @param rowIndex 行号（从0开始）
             * @param rowData 选择的行所代表的JSON对象
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onRowDblClick:function(rowIndex,rowData,event){
             *          alert('the '+rowIndex+'th row has been double clicked!city='+rowData.city);
             *      }
             *  });
             */
            onRowDblClick:function(rowIndex,rowData,event){},
            /**
             * 改变分页<b>之前</b>执行的方法。<b>注意：如果此方法返回false则不进行分页切换或跳转</b>。
             * @event
             * @type Function
             * @param type 切换类型，是'first'、'prev'、'next'、'last'、'input'之一。
             * @param newPage 要转到的页号（从1开始，第一页是1而不是0）
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onPageChange:function(type,newPage,event){
             *          alert('will goto page '+newPage);
             *      }
             *  });
             */
            onPageChange:function(type,newPage,event){},
            /**
             * 从后台取数成功时执行的方法。
             * @event
             * @type Function
             * @param data 取回来的数据（ 格式是{"total":35,"rows":[{"id":11,"city":"河南省安阳市","address":"电信"},{"id":12,"city":"北京市","address":"北龙中网科技有限公司"},{"id":13,"city":"澳大利亚","address":"CZ88.NET"}]}  ）。
             * @param testStatus 响应的状态（参考jQuery的$.ajax的success属性）
             * @param XMLHttpRequest XMLHttpRequest对象（参考jQuery的$.ajax的success属性）
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onSuccess:function(data,testStatus,XMLHttpRequest,event){
             *          alert('fetch data success,got '+data.rows+' rows');
             *      }
             *  });
             */
            onSuccess:function(data,testStatus,XMLHttpRequest,event){},
            /**
             * 从后台取数失败时执行的方法。
             * @event
             * @type Function
             * @param XMLHttpRequest XMLHttpRequest对象（参考jQuery的$.ajax的error属性）
             * @param testStatus 响应的状态（参考jQuery的$.ajax的error属性）
             * @param errorThrown 捕获的异常对象（参考jQuery的$.ajax的error属性）
             * @param event jQuery.Event对象
             * @default 无
             * @example
             *  $(".selector").omGrid({
             *      onError:function(XMLHttpRequest,textStatus,errorThrown,event){
             *          alert('fetch data error');
             *      }
             *  });
             */
            onError:function(XMLHttpRequest,textStatus,errorThrown,event){},
            /**
             * 数据已全部显示到表体中后执行的方法。
             * @event
             * @type Function
             * @param nowPage 当前页号(第一页是1第二页是2)
             * @param pageRecords 当前页的所有记录
             * @param event jQuery.Event对象
             * @default 无
             * @example
             * //数据显示完后自动选中所有地址是'电信'的行。
             *  $(".selector").omGrid({
             *      signleSelect:false,
             *      onRefresh:function(nowPage,pageRecords,event){
             *          var rows=[];
             *          $(pageRecords).each(function(i){
             *              if(this.address=='电信'){
             *                  rows.push(i);
             *              }
             *          });
             *          $('.selector').omGrid('setSelections',rows);
             *      }
             *  });
             */
            onRefresh:function(nowPage,pageRecords,event){}
        },
        //private methods
        _create:function(){
            var options=this.options,el=this.element.show() // show if hidden
                .attr({
                    cellPadding : 0,
                    cellSpacing : 0,
                    border : 0
                })
                .empty()
                .append('<tbody></tbody>');
            el.wrap('<div class="om-grid om-widget om-widget-content"><div class="bDiv"></div></div>').closest('.om-grid');
            var colModel=options.colModel;
            if(!$.isArray(colModel)){
                return; //如果colModel没设置或值不对，什么也不做
            }
            
            this.hDiv = $('<div class="hDiv om-state-default"></div>').append('<div class="hDivBox"><table cellPadding="0" cellSpacing="0"></table></div>');
            el.parent().before(this.hDiv);
            this.pDiv=$('<div class="pDiv om-state-default"></div>');
            el.parent().after(this.pDiv);
            
            var grid = el.closest('.om-grid');
            this.loadMask=$('<div class="gBlock"><div align="center" class="gBlock-valignMiddle" ><div class="loadingImg" style="display:block"/></div></div>')
                    .mousedown(function(e){
                        return false;  //禁用双击（默认双击全把div下面的内容全选）
                    })
                    .hide();
            grid.append(this.loadMask);
            
            this.titleDiv = $("<div class='titleDiv'></div>");
            grid.prepend(this.titleDiv);
            
            this.tbody=this.element.children().eq(0);
        },
        _init:function(){
        	var el=this.element,
        		options = this.options,
                grid = el.closest('.om-grid');
                
        	grid.width(options.width).height(options.height);
        	
            if(!$.isArray(this.options.colModel)){
                return; //如果colModel没设置或值不对，什么也不做
            }
            
            this.tbody.empty();
            $('table', this.hDiv).empty();
            this.pDiv.empty();
            this.titleDiv.empty();
            this._buildTableHead();
            this._buildPagingToolBar();
            this._buildLoadMask();
            this._bindSelectAndClickEnvent();
            this._bindScrollEnvent();
            this._makeColsResizable();
            this._buildTitle();
            
            var theadHeight = grid.children('.hDiv').outerHeight(),
                pagingToolBarHeight = grid.children('.pDiv').outerHeight() || 0,
                titleHeight = grid.children('.titleDiv').outerHeight() || 0,
                tbody = grid.children('.bDiv');
            tbody.height(grid.height()-theadHeight-pagingToolBarHeight-titleHeight);
            this.pageData={nowPage:1,totalPages:1};
            this._populate();
        },
        _buildTitle : function() {
            this.titleDiv.empty();
            if (this.options.title) {
                this.titleDiv.html("<div class='titleContent'>" + this.options.title + "</div>");
                this.titleDiv.css("height" , "");
            }else{
            	this.titleDiv.css("height" , 0);//在IE7中发现即使清空了内容，div高度仍为26px
            }
        },
        _buildTableHead:function(){
            var op=this.options,
                el=this.element,
                grid = el.closest('.om-grid'),
                cms=op.colModel,
                allColsWidth = 0, //colModel的宽度
                indexWidth = 0, //colModel的宽度
                checkboxWidth = 0, //colModel的宽度
                autoExpandColIndex = -1;
                thead=$('<thead></thead>');
                tr=$('<tr></tr>').appendTo(thead);
            //渲染序号列
            if(op.showIndex){
                var cell=$('<th></th>').attr({axis:'indexCol',align:'center'}).addClass('indexCol').append($('<div class="indexheader" style="text-align:center;width:25px;"></div>'));
                tr.append(cell);
                indexWidth=25;
            }
            //渲染checkbox列
            if(!op.singleSelect){
                var cell=$('<th></th>').attr({axis:'checkboxCol',align:'center'}).addClass('checkboxCol').append($('<div class="checkboxheader" style="text-align:center;width:17px;"><span class="checkbox"/></div>'));
                tr.append(cell);
                checkboxWidth=17;
            }
            //渲染colModel各列
            for (var i=0,len=cms.length;i<len;i++) {
                var cm=cms[i],cmWidth = cm.width || 60,cmAlign=cm.align || 'center';
                if(cmWidth == 'autoExpand'){
                    cmWidth = 0;
                    autoExpandColIndex = i;
                }
                var thCell=$('<div></div>').html(cm.header).css({'text-align':cmAlign,width:cmWidth});
                cm.wrap && thCell.addClass('wrap');
                var th=$('<th></th>').attr('axis', 'col' + i).addClass('col' + i).append(thCell);
                if(cm.name) {
                    th.attr('abbr', cm.name);
                }
                if(cm.align) {
                    th.attr('align',cm.align);
                }
                //var _div=$('<div></div>').html(cm.header).attr('width', cmWidth);
                allColsWidth += cmWidth;
                tr.append(th);
            }
            //tr.append($('<th></th'));
            el.prepend(thead);
            
            
            $('table',this.hDiv).append(thead);
            //修正各列的列宽
            if(autoExpandColIndex != -1){ //说明有某列要自动扩充
                var tableWidth=grid.width()-30,
                	toBeExpandedTh=tr.find('th[axis="col'+autoExpandColIndex+'"] div');
                //虽然toBeExpandedTh.parent().width()为0,但不同浏览器在计算下边的thead.width()竟然有差异(Chrome)，所以干脆先隐藏了，保证所有浏览器计算thead.width()值一致
                toBeExpandedTh.parent().hide();
                var usableWidth=tableWidth-thead.width();
                toBeExpandedTh.parent().show();
                if(usableWidth<=0){
                    toBeExpandedTh.css('width',60);
                }else{
                    toBeExpandedTh.css('width',usableWidth);
                }
            }else if(op.autoFit){
                var tableWidth=grid.width()-20,
                    usableWidth=tableWidth-thead.width(),
                    percent=1+usableWidth/allColsWidth,
                    toFixedThs=tr.find('th[axis^="col"] div');
                for (var i=0,len=cms.length;i<len;i++) {
                    var col=toFixedThs.eq(i);
                    col.css('width',parseInt(col.width()*percent));
                }
            }
            this.thead=thead;
            thead = null;
        },
        _buildPagingToolBar:function(){
            var op=this.options;
            if(op.limit<=0){
                return;
            }
            var self=this,
                el=this.element,
                pDiv= this.pDiv;
            pDiv.html('<div class="pDiv2">'+
                    '<div class="pGroup">'+
                    '<div class="pFirst pButton om-icon"><span class="om-icon-seek-start"></span></div>'+
                    '<div class="pPrev pButton om-icon"><span class="om-icon-seek-prev"></span></div>'+
                '</div>'+
                '<div class="btnseparator"></div>'+
                '<div class="pGroup"><span class="pControl"></span></div>'+
                '<div class="btnseparator"></div>'+
                '<div class="pGroup">'+
                    '<div class="pNext pButton om-icon"><span class="om-icon-seek-next"></span></div>'+
                    '<div class="pLast pButton om-icon"><span class="om-icon-seek-end"></span></div>'+
                '</div>'+
                '<div class="btnseparator"></div>'+
                '<div class="pGroup">'+
                    '<div class="pReload pButton om-icon"><span class="om-icon-refresh"></span></div>'+
                '</div>'+
                '<div class="btnseparator"></div>'+
                '<div class="pGroup"><span class="pPageStat"></span></div>'+
            	'</div>');
            var pageText = $.om.lang._get(op,"omGrid","pageText").replace(/{totalPage}/, '<span>1</span>').replace(/{index}/, '<input type="text" size="4" value="1" />');
            $('.pControl',pDiv).html(pageText);
            el.parent().after(pDiv);
            $('.pReload', pDiv).click(function() {
                self._populate();
            });
            $('.pFirst', pDiv).click(function() {
                self._changePage('first');
            });
            $('.pPrev', pDiv).click(function() {
                self._changePage('prev');
            });
            $('.pNext', pDiv).click(function() {
                self._changePage('next');
            });
            $('.pLast', pDiv).click(function() {
                self._changePage('last');
            });
            $('.pControl input', pDiv).keydown(function(e) {
                if (e.keyCode == $.om.keyCode.ENTER) {
					self._changePage('input');
				}
            });
            $('.pButton', pDiv).hover(function() {
                $(this).addClass('om-state-hover');
            }, function() {
                $(this).removeClass('om-state-hover');
            });
            this.pager=pDiv;
        },
        _buildLoadMask:function(){
            var grid = this.element.closest('.om-grid');
            this.loadMask.css({width:grid.width(),height:grid.height()});
        },
        _changePage : function(ctype) { // change page
            if (this.loading) {
                return true;
            }
            var el=this.element,
                op=this.options,
                grid = el.closest('.om-grid'),
                pageData = this.pageData,
                nowPage=pageData.nowPage,
                totalPages=pageData.totalPages,
                newPage = nowPage;
            this._oldPage = nowPage;//保存好旧的页数，有些插件如sort是需要用到的
            switch (ctype) {
                case 'first':
                    newPage = 1;
                    break;
                case 'prev':
                    if (nowPage > 1) {
                        newPage = nowPage - 1;
                    }
                    break;
                case 'next':
                    if (nowPage < totalPages) {
                        newPage = nowPage + 1;
                    }
                    break;
                case 'last':
                    newPage = totalPages;
                    break;
                case 'input':
                    var nv = parseInt($('.pControl input', el.closest('.om-grid')).val());
                    if (isNaN(nv)) {
                        nv = nowPage;
                    }
                    if (nv < 1) {
                        nv = 1;
                    } else if (nv > totalPages) {
                        nv = totalPages;
                    }
                    $('.pControl input', this.pDiv).val(nv);
                    newPage = nv;
                    break;
                default:
                    if (/\d/.test(ctype)) {
                        var nv = parseInt(ctype);
                        if (isNaN(nv)) {
                            nv = 1;
                        }
                        if (nv < 1) {
                            nv = 1;
                        } else if (nv > totalPages) {
                            nv = totalPages;
                        }
                        $('.pControl input', el.closest('.om-grid')).val(nv);
                        newPage = nv;
                    }
            }
            if (newPage == nowPage) {
                return false;
            }
            //触发事件
            if(this._trigger("onPageChange",null,ctype,newPage)===false){
                return;
            }
            //修改pageData
            pageData.nowPage=newPage;
            //翻页时去掉全选状态
            $('th.checkboxCol span.checkbox',grid).removeClass('selected');
            //刷新数据
            this._populate();
        },
        //刷新数据
        _populate : function() { // get latest data
            var self=this,
                el = this.element,
                grid = el.closest('.om-grid'),
                op = this.options,
                pageStat = $('.pPageStat', grid);
            if (!op.dataSource) {
                $('.pPageStat', grid).html(op.emptygMsg);
                return false;
            }
            if (this.loading) {
                return true;
            }
            var pageData = this.pageData,
                nowPage = pageData.nowPage || 1,
                loadMask = $('.gBlock',grid);
            //具备加载数据的前提条件了，准备加载
            this.loading = true;
            pageStat.html($.om.lang._get(op,"omGrid","loadingMsg"));
            loadMask.show();
            if ($.browser.opera) {
                $(grid).css('visibility', 'hidden');
            }
            var limit = (op.limit<=0)?100000000:op.limit;
            var param =$.extend(true,op.extraData,{
            	rpage: nowPage,
                start : limit * (nowPage - 1),
                limit : limit,
                _time_stamp_ : new Date().getTime()
            });
            $.ajax({
                type : op.method,
                url : op.dataSource,
                data : param,
                dataType : 'json',
                success : function(data,textStatus,request) {
                    var onSuccess = op.onSuccess;
                    if (typeof(onSuccess) == 'function') {
                        self._trigger("onSuccess",null,data,textStatus,request);
                    }
                    self._addData(data);
                    self._trigger("onRefresh",null,nowPage,data.rows);
                    loadMask.hide();
                    self.loading = false;
                },
                error : function(XMLHttpRequest, textStatus, errorThrown) {
                    pageStat.html($.om.lang._get(op,"omGrid","errorMsg")).css('color','red');
                    try {
                        var onError = op.onError;
                        if (typeof(onError) == 'function') {
                            onError(XMLHttpRequest, textStatus, errorThrown);
                        }
                    } catch (e) {
                        // do nothing 
                    } finally {
                        loadMask.hide();
                        self.loading = false;
                        return false;
                    }
                    
                }
            });
        },
        _addData:function(data){
            var op = this.options,
                el = this.element,
                grid = el.closest('.om-grid'),
                pageStat = $('.pPageStat', grid),
                preProcess = op.preProcess,
                pageData=this.pageData;
            //预处理
            preProcess && (data=preProcess(data));
            pageData.data=data;
            pageData.totalPages = Math.ceil(data.total/op.limit);
            //刷新分页条
            this._buildPager();
            this._renderDatas();
        },
        _buildPager:function(){
            var op=this.options;
            if(op.limit<=0){
                return;
            }
            var el=this.element,
                pager=this.pager,
                pControl=$('.pControl',pager),
                pageData = this.pageData,
                nowPage=pageData.nowPage,
                totalPages=pageData.totalPages,
                data=pageData.data,
                from=op.limit* (nowPage-1)+1,
                to=from-1+data.rows.length,
                pageStat='';
            if(data.total===0){
                pageStat=$.om.lang._get(op,"omGrid","emptyMsg");
            }else{
                pageStat = $.om.lang._get(op,"omGrid","pageStat").replace(/{from}/, from).replace(/{to}/, to).replace(/{total}/, data.total);
            }
            $('input',pControl).val(nowPage);
            $('span',pControl).html(totalPages);
            $('.pPageStat', pager).html(pageStat);
        },
        _renderDatas:function(from,to){
            var self=this,
                el=this.element,
                op=this.options,
                grid=el.closest('.om-grid'),
                gridHeaderCols=$('.hDiv thead tr:first th',grid),
                rows=this.pageData.data.rows,
                colModel=op.colModel,
                rowClasses=op.rowClasses,
                tbody=$('tbody',el).empty(),
                isRowClassesFn= (typeof rowClasses === 'function'),
                pageData = this.pageData,start=(pageData.nowPage-1)*op.limit;
            $.each(rows,function(i, rowData) {
                var rowCls = isRowClassesFn? rowClasses(i,rowData):rowClasses[i % rowClasses.length];
                var tr=$('<tr class="om-grid-row"></tr>').addClass(rowCls);
                var rowValues=self._buildRowCellValues(colModel,rowData,i);
                $(gridHeaderCols).each(function(){
                    var axis = $(this).attr('axis'),wrap=false,html;
                    if(axis == 'indexCol'){
                        html=i+start+1;
                    }else if(axis == 'checkboxCol'){
                        html = '<span class="checkbox"/>';
                    }else if(axis.substring(0,3)=='col'){
                        var colIndex=axis.substring(3);
                        html=rowValues[colIndex];
                        if(colModel[colIndex].wrap){
							wrap=true;
						} 
                    }else{
                        html='';
                    }
                    var td = $('<td></td>').attr({align:this.align,abbr:this.abbr}).addClass(axis).append($('<div></div>').html(html).addClass(wrap?'wrap':'').attr({'align':this.align}).width($('div',$(this)).width()));
                    tr.append(td);
                });
                tbody.append(tr);
            });
        },
        _buildRowCellValues:function(colModel,rowData,rowIndex){
            var len=colModel.length,values=[];
            for(var i=0;i<len;i++){
                var c=colModel[i],v=rowData[c.name],r=c.renderer;
                if(typeof r === 'function'){
                    v=r(v,rowIndex,rowData);
                }
                values[i]=v;
            }
            return values;
        },
        //滚动水平滚动条时让表头和表体一起滚动（如果没有这个方法则只有表体滚动，表头不会动，表头和表体就对不齐了）
        _bindScrollEnvent:function(){
            var hDiv=this.thead.closest('.hDiv');
            this.tbody.closest('.bDiv').scroll(function(){
                hDiv.scrollLeft($(this).scrollLeft());
            });
        },
        //绑定行选择/行反选/行单击/行双击等事件监听
        _bindSelectAndClickEnvent:function(){
            var self=this;
            //如果有checkbox列则绑定事件
            if(!this.options.singleSelect){ //可以多选
                // 全选/反选,不需要刷新headerChekcbox的选择状态
                $('th.checkboxCol span.checkbox',this.thead).click(function(){
                    var thCheckbox=$(this),trSize=$('tr',self.tbody).size();
                    if(thCheckbox.hasClass('selected')){ //说明是要全部取消选择
                        thCheckbox.removeClass('selected');
                        for(var i=0;i<trSize;i++){
                            self._rowDeSelect(i);
                        }
                    }else{ //说明是要全选
                        thCheckbox.addClass('selected');
                        for(var i=0;i<trSize;i++){
                            self._rowSelect(i);
                        }
                    }
                });
                //行单击,需要刷新headerChekcbox的选择状态
                this.tbody.delegate('tr.om-grid-row','click',function(event){
                    var row=$(this),index=row.index();
                    if(row.hasClass('om-state-highlight')){ //已选择
                        self._rowDeSelect(index);
                    }else{
                        self._rowSelect(index);
                    }
                    self._refreshHeaderCheckBox();
                    self._trigger("onRowClick",event,index,self._getRowData(index));
                });
                //行双击
                this.tbody.delegate('tr.om-grid-row','dblclick',function(event){
                    var row=$(this),index=row.index();
                    if(row.hasClass('om-state-highlight')){ //已选择
                        //do nothing
                    }else{
                        self._rowSelect(index);
                        self._refreshHeaderCheckBox();
                    }
                    self._trigger("onRowDbClick",event,index,self._getRowData(index));
                });
            }else{ //不可多选
                //行单击
                this.tbody.delegate('tr.om-grid-row','click',function(event){
                    var row=$(this),index=row.index();
                    if(row.hasClass('om-state-highlight')){ //已选择
                        // no need to deselect another row and select this row
                    }else{
                        var lastSelectedIndex = $('tr.om-state-highlight',self.tbody).index();
                        (lastSelectedIndex != -1) && self._rowDeSelect(lastSelectedIndex);
                        self._rowSelect(index);
                    }
                    self._trigger("onRowClick",event,index,self._getRowData(index));
                });
                
                //行双击,因为双击一定会先触发单击，所以对于单选表格双击时这一行一定是选中的，所以不需要强制双击前选中
                this.tbody.delegate('tr.om-grid-row','dblclick',function(event){
                    var index=$(this).index();
                    self._trigger("onRowDblClick",event,index,self._getRowData(index));
                });
            }
        },
        _getRowData:function(index){
            return this.pageData.data.rows[index];
        },
        _rowSelect:function(index){
             var el=this.element,
                op=this.options,
                tbody=$('tbody',el),
                tr=$('tr:eq('+index+')',tbody),
                chk=$('td.checkboxCol span.checkbox',tr);
            tr.addClass('om-state-highlight');
            chk.addClass('selected');
            this._trigger("onRowSelect",null,index,this._getRowData(index));
        },
        _rowDeSelect:function(index){
            var self=this,
                el=this.element,
                op=this.options,
                tbody=$('tbody',el),
                tr=$('tr:eq('+index+')',tbody),
                chk=$('td.checkboxCol span.checkbox',tr);
            tr.removeClass('om-state-highlight');
            chk.removeClass('selected');
            this._trigger("onRowDeselect",null,index,this._getRowData(index));
        },
        _refreshHeaderCheckBox:function(){
            var selectedRowSize=$('td.checkboxCol span.selected',this.tbody).size(),
                headerCheckbox = $('th.checkboxCol span.checkbox',this.thead);
            if(selectedRowSize < this.pageData.data.rows.length){
                headerCheckbox.removeClass('selected');
            }else{
                headerCheckbox.addClass('selected');
            }
        },
        //让列可以改变宽度（index列和checkbox列不可以改变宽度）
        _makeColsResizable:function(){
            var self=this,
                bDiv=self.tbody.closest('.bDiv'),
                grid=self.element.closest('.om-grid'),
                $titleDiv = this.titleDiv,
                pDiv=self.pager; 
            $('th[axis^="col"] div',self.thead).resizable({
                handles: 'e',//只可水平改变大小
                containment: 'document',
                minWidth: 60,
                resize: function(ui , event) {
                    var _this=$(this),abbr=_this.parent().attr('abbr'),dataCells=$('td[abbr="'+abbr+'"] > div',self.tbody),hDiv=self.thead.closest('.hDiv');
                    _this.width(ui.size.width).height('');
                    dataCells.width(ui.size.width).height('');
                    bDiv.height(grid.height()-$titleDiv.outerHeight()-hDiv.outerHeight()-pDiv.outerHeight());
                }
            });
        },

        //public methods
        /**
         * 修改取数url并立即刷新数据。一般用于查询操作。比如开始时取数url是data.json则后台实际收到data.json?start=0&limit=15这样的请求。查询时使用setData方法将取数url改成data.json?queryString=admin，后台实际收到data.json?queryString=admin&start=0&limit=15这样的请求，后台根据参数queryString来做查询即可。
         * @name omGrid#setData
         * @function
         * @param url 新的数据源url
         * @returns jQuery对象
         * @example
         *  //使用新的url来取数，设置完后会立即开始刷新表格数据。
         *  $('.selector').omGrid('setData', 'newgriddata.json');
         */
        setData:function(url){
            this.options.dataSource=url;
            this.pageData={nowPage:1,totalPages:1};
            this._populate();
        },
        /**
         * 获取表格JSON数据。<br/>
         *     
         * @name omGrid#getData
         * @function
         * @returns 如果没有设置preProcess则返回由后台返回来的对象。如果有preProcess则返回处理后的对象
         * @example
         * //获取grid的数据源
         * var store = $('.selector').omGrid('getData');
         * 
         * 
         */
        getData:function(){
            return this.pageData.data;
        },
        /**
         * 使用getData方法的结果重新渲染数据。<b>注意：该方法并不会发送Ajax请求，而且如果表格当前正在加载数据（loadmask还未消失）的话则什么也不做直接返回</b>。
         * @name omGrid#refresh
         * @function
         * @returns jQuery对象
         * @example
         * //根据当前grid数据模型中的数据，重新刷新grid
         * $('.selector').omGrid('refresh');//注意refresh没有传入参数
         * 
         */
        refresh:function(){
            // 修改数据模型后可以用此方法来强制刷新（仅客户端行为,不向后台发送请求）
            if (this.loading) {
                return true;
            }
            this.loading = true;
            var op=this.options;
            $('.pPageStat', this.pager).html($.om.lang._get(op,"omGrid","loadingMsg"));
            this.loadMask.show();
            this._buildPager();
            this._renderDatas();
            this._trigger("onRefresh",null,this.pageData.nowPage || 1,this.pageData.data.rows);
            this.loadMask.hide();
            this.loading = false;
        },
        /**
         * 刷新表格。如果没有参数则刷新当前页，如果有参数则转到参数所表示的页（如果参数不合法会自动进行修正）。<b>注意：该方法会发送Ajax请求，而且如果表格当前正在加载数据（loadmask还未消失）的话则什么也不做直接返回</b>。
         * @name omGrid#reload
         * @function
         * @param page 要转到的页，参数为空表示刷新当前页。如果参数不是数字或者小于1则自动修正为1，如果参数大于总页数则自动修正为总页数。
         * @returns jQuery对象
         * @example
         * $('.selector').omGrid('reload');//刷新当前页
         * $('.selector').omGrid('reload',3);//转到第3页
         * 
         */
        reload:function(page){
            if (this.loading) {
                return true;
            }
            if(typeof page !=='undefined'){
                page=parseInt(page) || 1;
                if(page<0){
                    page = 1;
                }
                if(page>this.pageData.totalPages){
                    page=this.pageData.totalPages;
                }
                this.pageData.nowPage = page;
            }
            //相当于goto(page) and reload()，会转到那一页并重新刷新数据（向后台发送请求）
            //没有参数时刷新当前页
            this._populate();
        },
        /**
         * 选择行。<b>注意：传入的参数是序号（第一行是0第二行是1）数组（比如[0,1]表示选择第一行和第二行）；要想清除所有选择，请使用空数组[]作为参数；只能传入序号数组，如果要做复杂的选择算法，请先在其它地方算好序号数组后后调用此方法；此方法会清除其它选择状态，如选择第1,2行然后setSelections([3])最后结果中只有第3行，如setSelections([3,4]);setSelections([5,6])后只会选择5,6两行</b>。
         * @name omGrid#setSelections
         * @function
         * @param indexes 序号（第一行是0第二行是1）数组。
         * @returns jQuery对象
         * @example
         * //选择表格中第二行、第三行、第五行
         * $('.selector').omGrid('setSelections',[1,2,4]);
         * 
         */
        setSelections:function(indexes){
            var self=this;
            if(!$.isArray(indexes)){
                indexes=[indexes];
            }
            var selected=this.getSelections();
            $(selected).each(function(){
                self._rowDeSelect(this);
            });
            $(indexes).each(function(){
                self._rowSelect(this);
            });
            self._refreshHeaderCheckBox();
        },
        /**
         * 获取选择的行的行号或行记录。<b>注意：默认返回的是行序号组成的数组（如选择了第2行和第5行则返回[1,4]），如果要返回行记录JSON组成的数组需要传入一个true作为参数</b>。
         * @name omGrid#getSelections
         * @function
         * @param needRecords 参数为true时返回的不是行序号数组而是行记录数组。参数为空或不是true时返回行序号数组。
         * @returns jQuery对象
         * @example
         * var selectedIndexed = $('.selector').omGrid('getSelections');
         * var selectedRecords = $('.selector').omGrid('getSelections',true);
         * 
         */
        getSelections:function(needRecords){
            //needRecords=true时返回Record[],不设或为false时返回index[]
            var self=this,trs=$('tr.om-state-highlight',this.tbody),result=[];
            if(needRecords){
                var rows=self.pageData.data.rows;
                trs.each(function(){
                    result[result.length]=rows[$(this).index()];
                });
            }else{
                trs.each(function(){
                    result[result.length]=$(this).index();
                });
            }
            return result;
        },
        destroy:function(){
        	var $el = this.element;
        	$el.closest('.om-grid').after($el).remove();
        }
    });
})(jQuery);