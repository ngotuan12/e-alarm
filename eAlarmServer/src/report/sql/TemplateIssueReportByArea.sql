SELECT  `device_issue` . * ,  `device`.`code` ,  `device`.`area_code` ,  `reason`.`name` 
FROM  `device_issue` 
LEFT JOIN  `device` ON  `device_issue`.`device_id` =  `device`.`id` 
LEFT JOIN  `reason` ON  `device_issue`.`reason_id` =  `reason`.`id` 