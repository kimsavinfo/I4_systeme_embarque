<?php
/** 
	ACCES POUR LA BASE DE DONNEES
*/
function Sql_getServer()
{
	return "localhost";
	// return "mysql51-132.perso";
}
function Sql_getLogin()
{
	return "kimsavinbdd";
}
function Sql_getPassword()
{
	return "ovhbdd314";
}
function Sql_getDatabase()
{
	return "kimsavinbdd";
}

/** 
	LIB POUR LES REQUETES
*/
function SQL_execute($requete_sql)
{
	$bdd_connexion = new mysqli(Sql_getServer(), Sql_getLogin(), Sql_getPassword(), Sql_getDatabase());
	if ($bdd_connexion->connect_error) 
	{
		die("Echec de la connexion: " . $bdd_connexion->connect_error);
	}
	else
	{
		if ($result = $bdd_connexion->query($requete_sql)) 
		{
			// OK
		}
		else
		{
			die("Erreur requete: " . $bdd_connexion->error);
		}
	}
}

?>