<?php 
include_once 'conexion.php';
///datos a mostrar.
$query="select * from personas order by dni";
$query_execute=mysql_query($query) or die(mysql_error());
 $json=array();
 if (mysql_num_rows($query_execute)) {
 	# code...
 	while ($row=mysql_fetch_assoc($query_execute)) {
 		# code...
 		$json['personas'][]=$row;
 	}
 }
 mysql_close();
 echo json_encode($json);
 ?>