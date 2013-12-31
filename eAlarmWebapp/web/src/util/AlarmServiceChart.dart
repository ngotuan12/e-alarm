part of EASUtil;

class AlarmServiceChart
{
	var jsOptions;
	var jsTable;
	var jsChart;
	

	/*@author DIENND
	 * @since 14/12/2013
	 * Draw chart in infoWindow of google map
	 */
	AlarmServiceChart(Element element, Map options,Map device)
	{
		List<Map> InforDevices=device["list"];
		//querySelector("#nameDevice").text=device["address"];
		final data=[['Ngày', InforDevices[0]["name"], InforDevices[1]["name"],'Độ Rung'],['2 hôm trước',InforDevices[0]["value"],InforDevices[1]["value"],10],['Hôm qua',  2,      87,6],['Hôm nay',  21,       65,9]];
		final vis = context["google"]["visualization"];
		jsTable = vis.callMethod('arrayToDataTable', [new JsObject.jsify(data)]);
		jsChart = new JsObject(vis["LineChart"], [element]);
		jsOptions = new JsObject.jsify(options);
		draw();
	}
		
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