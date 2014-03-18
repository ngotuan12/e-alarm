part of EASUtil;

class AlarmServiceChart
{
	var jsOptions;
	var jsTable;
	var jsChart;
	JsObject vis;
	Element ChartMap;
	List data;
	var option;
	/*
	 * 
	 */
	static updateChart(int currentValue)
	{
			
	}
	/*@author DIENND
	 * @since 14/12/2013
	 * Draw chart in infoWindow of google map
	 */
	AlarmServiceChart(Element element,Map device)
	{
		ChartMap=element;
		//style 
		 option = 
			{
				'hAxis': 
				{
					'title': '','titleTextStyle':
					{
						'color': 'red'
					},
					'gridlines':
					{
						'color':'#8ec657'
					}
				},
				'backgroundColor':
				{
					'fill': '#1f1f1f', 
					'stroke': '#1f1f1f', 
					'strokeWidth': 15
					},
				'connectSteps': false,
				'colors': //color for chart
					[
						'#8ec657'
					],
				//'isStacked': true,
				'vAxis': 
				{
					'minValue': 0,
					'gridlines':
					{
						'color':'#8ec657'
					}
				},
				'chartArea': //fix width,height for chart
				{
					'left':20,
					'width':'96%',
					'height':'85%'
				}
			}; 
			data=[['Year', 'Giá trị :']];
			Random random=new Random();
			for(int i=0;i<10;i++)
				data.add([i,random.nextInt(90)]);
			vis = context["google"]["visualization"];
			jsTable = vis.callMethod('arrayToDataTable', [new JsObject.jsify(data)]);
			jsChart = new JsObject(vis["AreaChart"], [ChartMap]);//type chart
			jsOptions = new JsObject.jsify(option);
			Timer timer = new Timer.periodic(new Duration(seconds: 1), (Timer timer){
				data.add([data.length,random.nextInt(90)]);
				jsTable = vis.callMethod('arrayToDataTable', [new JsObject.jsify(data)]);
				draw();
			});
			draw();
	}
	/*
	 * 
	 */
	
	void draw() 
	{
		jsChart.callMethod('draw', [jsTable, jsOptions]);
	}
	
	static Future load() 
	{
		Completer c = new Completer();
		context["google"].callMethod('load',
		['visualization', '1', new JsObject.jsify({
			'packages': ['corechart'],
			'callback': new JsFunction.withThis(c.complete)
		})]);
		return c.future;
	}
}