<?php
require_once("sql.inc.php");

class User
{
	var $id_user;
	var $user_login;
	var $user_password;
	var $user_upd;
}

function User_chercherRequete($args)
{
	$sql = "";

	if (!isset($args['user_login']))
	{
		$sql = " SELECT count(*) id_user 
					FROM qrc_users
					WHERE 1";
	} 
	else 
	{
		$sql = " SELECT * 
					FROM qrc_users
					WHERE 1";

		$condition="";

		if (isset($args['user_login']))
			$condition .= " AND user_login LIKE '".htmlspecialchars($args['user_login'])."' ";
		if (isset($args['user_password']))
			$condition .= " AND user_password LIKE '".htmlspecialchars($args['user_password'])."' ";

		$sql .= $condition;

		if (isset($args['order_by']) && !isset($args['asc_desc']))
			$sql .= " ORDER BY ".$args['order_by']." ASC";
		if (isset($args['order_by']) && isset($args['asc_desc']))
			$sql .= " ORDER BY ".$args['order_by']." ".$args['asc_desc'];

		if (isset($args['limit']) && !isset($args['start']))
			$sql .= " LIMIT ".$args['limit'];

		if (isset($args['limit']) && isset($args['start']))
			$sql .= " LIMIT ".$args['start'].",".$args['limit'];
	}

	return $sql;
}

function Users_chercherMYSQLI($args)
{
	$tab_result = array();

	$bdd_connexion = new mysqli(Sql_getServer(), Sql_getLogin(), Sql_getPassword(), Sql_getDatabase());
	if ($bdd_connexion->connect_error) 
	{
		die("Echec de la connexion: " . $bdd_connexion->connect_error);
	}
	else
	{
		$requete_sql = User_chercherRequete($args);

		/* Requête "Select" retourne un jeu de résultats */
		if ($result = $bdd_connexion->query($requete_sql)) 
		{
			while ($row = $result->fetch_assoc()) 
			{
				$id = $row['id_user'];
				$tab_result[$id]["id_user"]	= $id;
				$tab_result[$id]["user_login"]			= $row['user_login'];
				$tab_result[$id]["user_password"]		= $row['user_password'];
				$tab_result[$id]["user_upd"]			= $row['user_upd'];
			}
			
			$result->free();
		}
		else
		{
			die("Erreur requete: " . $bdd_connexion->error);
		}
	}

	$bdd_connexion->close();

	return $tab_result;
}

?>