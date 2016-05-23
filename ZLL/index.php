<?php
	$id = $_GET['id'];
	if(!$id){
		header("location:http://coderr.sinaapp.com?zll");
		exit;
	}
	$pt = $_GET['pt'];
	$domain = 'coderr';
	$idPath = 'zll/'.$id;
	$s = new SaeStorage();
	$doc = new DOMDocument();
	if(!$s->fileExists($domain,$idPath) || !$doc->loadXML($s->read($domain,$idPath))){
		echo 'id is invalid';
		exit;
	}
	$root = $doc->documentElement;
	$createtime = $root->getAttribute('createtime');
	$ip = $root->getAttribute('ip');
	if(!$ip){
		echo 'ip is null';
		exit;
	}
	$pis = $root->getAttribute('pt');
	if($pt && $pis){
		$pis = explode(";",$pis);
		foreach($pis as $pi){
			if(!$pi)	continue;
			$ss = explode(":",$pi);
			if(!$ss || count($ss) != 3)	continue;
			if($ss[0] == $pt){
				header('location:http://'.$ip.':'.$ss[1].$ss[2]);
				exit;
			}
		}
	}
	if(isset($_GET["p"])){
		 $as = $root->attributes;
		 if(!$as){
		 	echo 'Error';
			exit;
		 }
		 foreach($as as $a){
			echo $count."<font style='color:red;'>".$a->nodeName.'</font> -> '.$a->nodeValue.'<br />';
		 }
	}elseif($createtime && $ip){
		echo isset($_GET["t"]) ? $createtime.'|'.$ip : 'ip:'.$ip;
	}else{
		echo 'Error';
	}
?>