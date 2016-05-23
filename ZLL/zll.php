<?php
	$id = $_GET['id'];
	if(!$id){
		echo 'id is null';
		exit;
	}
	$domain = 'coderr';	
	$idPath = 'zll/'.$id;
	$s = new SaeStorage();
	$doc = new DOMDocument();
	$doc->formatOutput = true;
	if(!$s->fileExists($domain,$idPath)){
		$root = $doc->createElement('properties');
		$doc->appendChild($root);
		if(!$s->write($domain,$idPath,$doc->saveXML())){
			echo 'Error';
			return;
		}
	}
	$doc->loadXML($s->read($domain,$idPath));
	$root = $doc->documentElement;
	
	foreach($_GET as $key => $value){
		if($key=='id' || $key=='random')	continue;
		$kv = $doc->createAttribute($key);
		$kv->appendChild($doc->createTextNode($value));
		$root->appendChild($kv);
	}
	
	$ip = $doc->createAttribute('ip');
	$ip->appendChild($doc->createTextNode($_SERVER["REMOTE_ADDR"]));
	$root->appendChild($ip);
	
	$createtime = $doc->createAttribute('createtime');
	$createtime->appendChild($doc->createTextNode(date('Y-m-d H:i:s',time())));
	$root->appendChild($createtime);
	
	if(!$s->write($domain,$idPath,$doc->saveXML())){
		echo 'Error';
		exit;
	}
	echo 'Success';
?>