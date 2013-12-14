part of EASUtil;

class AlarmServiceChart
{
	var jsOptions;
	var jsTable;
	var jsChart;
	
	// Access to the value of the gauge.
	num _value;
	get value => _value;
	set value(num x) 
	{
		_value = x;
		draw();
	}
	/*@author DIENND
	 * @since 14/12/2013
	 * Draw chart in infoWindow of google map
	 */
	AlarmServiceChart(Element element, String title, this._value, Map options)
	{
		final data = [['Ngày', 'Nhiệt độ', 'Độ Ẩm','Độ Rung'],['2 hôm trước',  23,      80,10],['Hôm qua',  2,      87,6],['Hôm nay',  21,       65,9]];
		final vis = context["google"]["visualization"];
		jsTable = vis.callMethod('arrayToDataTable', [new JsObject.jsify(data)]);
		jsChart = new JsObject(vis["LineChart"], [element]);
		jsOptions = new JsObject.jsify(options);
		draw();
	}
		
	void draw() 
	{
		jsTable.callMethod('setValue', [0, 1, value]);
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