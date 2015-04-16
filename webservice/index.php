<?php

require_once("models/user.inc.php");

$output = array();

if( !empty($_GET['login']) && !empty($_GET['password']) )
{
	$args_user = array();
	$args_user['user_login'] = $_GET['login'];
	$args_user['user_password'] = $_GET['password'];
	$users = Users_chercherMYSQLI($args_user);

	if(count($users) == 1)
	{
		$user_connected  = array_pop($users);
		$bdd_row["reponse"] = utf8_encode("ok");
	}
}

$output[] = $bdd_row;
print(json_encode($output));
?>