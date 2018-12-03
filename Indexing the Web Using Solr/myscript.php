<?php
header('Content-Type: text/html; charset=utf-8');
$search_limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$rank = array('sort'=>'pageRankFile desc');
$results = false;
$arr = array();
$f = fopen("URLtoHTML_nypost.csv","r");
if($f!==false){
while($line_csv = fgetcsv($f,0,","))
{
	
	$key = $line_csv['0'];
	$value = $line_csv['1'];
	$arr[$key] = $value;
}
fclose($f);
}
if ($query==true)
{
 require_once('solr-php-client/Apache/Solr/Service.php');
 $solr = new Apache_Solr_Service('localhost', 8983,'solr/myexample');
 if (get_magic_quotes_gpc() == 1)
 {
	 $query = stripslashes($query);
 }
 try { 
				if($_REQUEST['sort']=='solr') {
			// if($_GET['pageRankType'] == "pageRank") {
				//$additionalParameters = array('fq'=>'og_url:[* TO *]');
                $results = $solr->search($query, 0, $search_limit);  
            }
            else {
                // $results	= $solr->search($query,	0,	$limit);
                $additionalParameters = array('sort'=>'pageRankFile desc');
                //$additionalParameters = array('sort'=>'pageRankFile desc');
                $results = $solr->search($query, 0, $search_limit, $additionalParameters);
            }
  } 
  catch (Exception $e) { 
		die("<html><head><title>Exception on Search</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
  finally{

  } 
}
?>

<html> <head> <title>Solr exercise</title> <style>body {
    background-color: #AFEEEE;
}</style></head>
<body><center> <form accept-charset="utf-8" method="get"> <br><label for="q">Search:</label> 
<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/> 
<br>
<br> 
<table border="1" style="border-collapse: collapse">
<tr>
<td><input type="radio" name="sort" value="solr" <?php if(!isset($_REQUEST[ 'sort']) || $_REQUEST[ 'sort']=='solr' ) echo "checked"; ?>></td>
<td>Solr Default(LUCENE)</td>
</tr>
<br> 
<tr>
<td><input type="radio" name="sort" value="pageRank" <?php if($_REQUEST[ 'sort']=='pageRank' ) echo "checked"; ?>></td>
<td>External PageRank</td>
</tr>
</table>
          
            <br>
<input type="submit"/>

</form> 
</center>
<?php 
if ($results) { 
	$total = (int) $results->response->numFound; 
	$start = min(1, $total); 
	$end = min($search_limit, $total); 
?> 
<div>
	Total number of Results: <?php echo $total;?><br>
	Results showing <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:
</div> 
<ol> 
	<?php 
		foreach ($results->response->docs as $doc) {
	?> 
	<li> 
		 <table>
		<?php
		 
		$docId = "N/A";
		$docDesc = "N/A";
		$docLink="N/A";
		$docTitle = "N/A";
		$docog_Url="N/A";
		foreach ($doc as $field => $value) { 
			if($field== "id" ){
				$docId=$value;
			}
			if($field == "title"){
				$docTitle=$value;
			}
			if($field == "og_description"){
				$docDesc=$value;
			}
			if($field == "og_url"){
				$docog_Url=$value;
			}
		
		  } 
			echo "<tr border='1px'>";

		    $docLink = $arr[trim(substr($docId,35))];
		    $docog_Url = ($docog_Url =="N/A") ? $docLink : $docog_Url ;
            echo "<B>ID: </B>".$docId."<br>";	          
			echo "<B>TITLE:</B>".'<a href='.$docLink.'>'.$docTitle.'</a>'."<br>";
			echo "<B>URL: </B>".'<a href='.$docog_Url.'>'.$docog_Url.'</a>'."<br>";
				
			echo "<B>DESCRIPTION:</B> ".$docDesc."<br>";

			echo "</tr>";
			
		 ?>
			

		</table> 
	</li> 
	<?php } ?> 
</ol> 
<?php } ?> 
</body> 
</html>