<?php

require_once("models/user.inc.php");

$logs = "INIT";
if( !empty($_GET['login']) && !empty($_GET['password']) )
{
	$args_user = array();
	$args_user['user_login'] = $_GET['login'];
	$args_user['user_password'] = $_GET['password'];
	$users = Users_chercherMYSQLI($args_user);

	if(count($users) == 1)
	{
		$user_connected  = array_pop($users);
		print_r($user_connected);
		$logs = "OK";
	}
	else
	{
		$logs = "nb user = ".count($users);
	}
}
else
{
	$logs = "EMPTY";
}

?>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=ISO-8859-1"> 
	<title>Systeme Embarque - QRCode</title>
</head>
<body>
	<h1>QRCHEESE</h1>
	<?php echo $logs; ?>
</body>
</html>