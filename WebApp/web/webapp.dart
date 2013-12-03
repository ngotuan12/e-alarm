	import 'dart:html';
	import 'dart:convert';
	
	void main() 
	{
		querySelector("#sample_text_id")
		..text = "Click me!"
		..onClick.listen(reverseText);
	}
	/*
	 * 
	 */
	void reverseText(MouseEvent event) 
	{
		JSON.decode("fdsfds");
		var text = querySelector("#sample_text_id").text;
		var buffer = new StringBuffer();
		for (int i = text.length - 1; i >= 0; i--) 
		{
			buffer.write(text[i]); 			
		}
		querySelector("#sample_text_id").text = buffer.toString();
	}
