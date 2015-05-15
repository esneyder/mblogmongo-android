<?PHP
include_once 'conexion.php';
//variables que almacenan los valores que enviamos por nuestra app, (observar que se llaman igual en nuestra app y aqui)
$nombre=$_POST['nombre'];
$dni=$_POST['dni'];
$telefono=$_POST['telefono'];
$email=$_POST['email'];

$query_search = "insert into personas(nombre,dni,telefono,email) values ('".$nombre."','".$dni."','".$telefono."','".$email."')";//Sentencia sql a realizar
$query_exec = mysql_query($query_search) or die(mysql_error());//Ejecuta la sentencia sql.
?>
