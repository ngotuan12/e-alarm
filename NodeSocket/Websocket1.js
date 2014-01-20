/*
 * node-ws - pure Javascript WebSockets server
 * Copyright Bradley Wright <brad@intranation.com>
 */
 
// Use strict compilation rules - we're not animals
var serverIPAddress = "127.0.0.1";
var net = require('net');
var util = require("util") ;
var gjson = require("./json_sans_eval.js");
var gateways = new Array;
var dispatchers = new Array;
var monitors = new Array;
var server;
var gateway;
var log_ob;
var func = require("./func.js");
connDB = require("./AppServer.js").connDB;

connDB.connect(function(err) 
{
	if(err) throw err;
	util.log('Connected to database...');
});

//util.log(String.fromCharCode(0x04));
var WebsocketServer = net.createServer(function (socket) {
	socket.checkConnect = false;
 	console.log('connected');
 	socket.setTimeout(150000);
 	socket.isGetServer = false;
 	socket.setNoDelay(false);
	//client.setTimeout(150000);
	socket.setEncoding("utf8");
	socket.setKeepAlive(false);
 	
	
	socket.on("close", function () {
		util.log('disconnected');
		if(socket.isConnect)
		{
			if(socket.gatewayinfo.type == "1")
			{
				//remove gateway
				for(var i=0;i<gateways.length;i++)
				{
					if(gateways[i].gatewayinfo.id==socket.gatewayinfo.id)
					{
						gateways.splice(i,1);
						updateGatewayStatus(socket,false);
						break;
					}
					
				}
			}
			else if(socket.gatewayinfo.type == "2")
			{
				//remove monitor
				for(var i=0;i<monitors.length;i++)
				{
					if(monitors[i].gatewayinfo.id==socket.gatewayinfo.id)
					{
						monitors.splice(i,1);
						break;
					}
				}
			}
			else if(socket.gatewayinfo.type == "3")
			{
				//remove monitor
				for(var i=0;i<dispatchers.length;i++)
				{
					if(dispatchers[i].gatewayinfo.id==socket.gatewayinfo.id)
					{
						dispatchers.splice(i,1);
						break;
					}
				}
			}
		}
  	});
	
	socket.on("timeout", function () {
		util.log("timeout");	
	});

	socket.on("error", function (excep) {
		util.log(excep);
	});
    socket.on('data', function (data) {
    	try
    	{
			util.log(typeof data);
	    	//util.log("Data"+data);
	    	//util.log(data.toString().length);
	    	var str = data.toString();
	    	var strReuest = str.substring(1,str.length-1);
			util.log(strReuest);
	    	//util.log("Request"+strReuest);
	    	var request = gjson.jsonParse(strReuest);
	    	//util.log(request.cmd);
	    	util.log("cmd: "+request.cmd);
	    	if(!socket.isConnect&&request.cmd!="announce")
			{
				socket.destroy();
			}
			
			switch(request.cmd)
	    	{
	    		case "announce":
					util.log('-------------announce--------------');
					util.log('---------check_for_mac-------------');
					checkMac(request,socket);					
					break;
				case "get_time":
					util.log("--------------------");
					util.log("get_time: "+data);
					util.log("--------------------");
					var date = new Date();
					var gwRequest ={"ver": 1, "type": "response", "cmd": "get_time","body" : {"Year":date.getFullYear(),"Month":date.getMonth()+1,"Day":date.getDate(),"Hour":date.getHours(), "Minutes":date.getMinutes()," Seconds":date.getSeconds()}};
					sendGatewayCommand(gwRequest,socket);
					break;
				case "send_gw_request":
	    			var g_mac = request.G_MAC;
					var gateway = findGatewayByGMac(g_mac);
					if(gateway==null)
					{
						socket.write("gateway not found!");
					}
					else
					{
						sendGatewayCommand(gjson.jsonParse(request.body),gateway);
						socket.on_gateway = g_mac;
						socket.on_cmd = request.body.cmd;
					}
	    			break;
	    		default:
					if(socket.gatewayinfo.type == "1"&& request.type == "response")
					{
						request.mac_add = socket.gatewayinfo.mac_add; 
						for(var i=0;i<dispatchers.length;i++)
						{
							if(dispatchers[i].on_gateway == socket.gatewayinfo.mac_add&&
							dispatchers[i].on_cmd = request.cmd)
							{
								//write to dispatcher wait response
								dispatchers[i].write(JSON.stringify(request));
							}
						}
					}
	    			break;
				sendToMonitor(str);
			}
		}
		catch(e)
		{
			util.log(e.stack);
			socket.write(e.stack);
		}
    });
});
function findGatewayByGMac(gmac)
{
	for(var i=0;i<gateways.length;i++)
	{
		if(gateways[i].gatewayinfo.mac_add == gmac)
		{
			return gateways[i];
		}
	}
	return null;
}
function sendGatewayRequest(request)
{
	//char byteAdding = 0x01;
	//char charVersion = '1'; 
	//byte byteFooter = 0x04;
	var strRequest = JSON.stringify(request);
	//strRequest = String.valueOf((char)byteAdding)+  String.valueOf(charVersion) + strRequest +String.valueOf((char)byteFooter);
	strRequest = String.fromCharCode(0x01)+'1'+strRequest+String.fromCharCode(0x04)
	if(gateway!=null)
	{
		gateway.write(strRequest);
	}
	util.log("GWRequest:"+strRequest);
}
function getCurrentReading(socket)
{
	
	var str = "{ \"ver\": 1, "+
	"\"type\": \"request\", "+
	"\"cmd\": \"device_command\", "+
	"\"body\" : { "+
	"\"device_id\": 3, "+
	"\"cmd\" : \"get_current_reading\""+
	"} "+ 
	"} ";
	socket.write(str);
}
function setTime(socket)
{
	//util.log(new Date().getTime());
	//util.log(Math.round(+new Date()/1000));
	var request ={"ver": 1, 
	"type": "response", 
	"cmd": "get_time",
		"body" : {
			"value": Math.round(+new Date()/1000)
		}
	};
	socket.write("☺1"+JSON.stringify(request)+"♦");
}
function updateData()
{

	var strSQL = "SELECT id,device_id,device_pro_id,`value` FROM gateway WHERE device_id = ? ";
	connDB.query(strSQL,[log_ob.gateway_id], function(err) 
			{
			});
}

function updateGatewayStatus(socket,turn)
{	
	if(turn)
	{
		var strSQL = "Update gateway set connected_server = true,status = 1,ip_add ="+ serverIPAddress +" WHERE id =" + socket.gatewayinfo.id;
		connDB.query(strSQL, function(err) 
		{
			if (err) 
			{
				util.log(err);
				return;
			}
		});
		util.log("Update database when turn gateway on");
		
	}else
	{
		var strSQL = "Update gateway set connected_server = null,status = 0,ip_add = null WHERE id =" + socket.gatewayinfo.id ;
		connDB.query(strSQL, function(err) 
		{
			if (err) 
			{
				util.log(err);
				return;
			}
		});
		util.log("Update database when turn gateway off");
	}
}
function checkMac(request,socket) 
{
	util.log(request.body.G_MAC);
	var strSQL = "SELECT id,mac_add,connected_server,`status`,`type` FROM gateway WHERE mac_add = ? ";
	connDB.query(strSQL,[request.body.G_MAC], function(err, rows, fields) 
	{
		if (err) 
		{
			util.log(err);
			socket.isok = false;
			return;
		}
		
		//Neu chua khoi tao du lieu
		if(rows.length!=0)
		{
			socket.isok = true;
			var gatewaydata = rows[0];
			
			var now = new Date();
			create_log(gatewaydata.id,now,"connect",request.body.G_type);
			
			socket.gatewayinfo = rows[0];
			if(socket.gatewayinfo.type == "1")
			{
				gateways.push(socket);
			}
			else if(socket.gatewayinfo.type == "2")
			{
				monitors.push(socket);
			}
			else if(socket.gatewayinfo.type == "3")
			{
				dispatchers.push(socket);
			}
			connDB.query("INSERT INTO gateway_log SET gateway_id = ?,command = ?,type = ? ",[log_ob.gateway_id,log_ob.command,log_ob.type], function(err) 
			{
				if (err) 
				{
					util.log(err);
					return;
				}
			});
			util.log("---------connect-success-------------");
			var response = { "type":"response","cmd": "announce","body" : {"result": "ok"}};
			sendGatewayCommand(response,socket);
			socket.isConnect = true;
			updateGatewayStatus(socket,true);
		}		
		else 
		{
			util.log("---------connect-failed-------------");
			socket.destroy();
		}
	});
}
function sendGatewayCommand(response,socket)
{
	var strResponse = JSON.stringify(response);
	if(socket.gatewayinfo.type == "1")
	{
		strResponse = String.fromCharCode(0x01)+strResponse+String.fromCharCode(0x0A)+String.fromCharCode(0x0D);
	}
	else if(socket.gatewayinfo.type == "2")
	{
		strResponse = strResponse+"\n";
	}
	if(socket!=null)
	{
		socket.write(strResponse);
	}
	util.log("response:"+strResponse);
}

function sendToMonitor(str)
{
	for(var i=1;i<monitors.length;i++)
	{
		sendGatewayCommand(str,monitors[i]);
	}
}
function create_log(gateway_id,issue_date,command,type)
{
	log_ob = new Object();
	log_ob.gateway_id = gateway_id;
	log_ob.issue_date = issue_date;
	log_ob.command = command;
	log_ob.type = type;
}

WebsocketServer.listen(32432, "127.0.0.1");
console.log('websocket server start on port 32432');