SELECT  d1.* ,  d.code,d.`name` ,  d.area_code 
FROM  device_issue d1,device d
WHERE d1.device_id =  d.id 
<%p_device_id%> 
ORDER BY d.code