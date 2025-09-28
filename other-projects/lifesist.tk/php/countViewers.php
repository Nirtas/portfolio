<?php
    $f=fopen("viewers.txt","a+");
    $count=fread($f,100);
    if ($count == 0)
        $count++;
    echo "Вы - $count покупатель. Удачных покупок!";        
    $count++;
    ftruncate($f,0);
    fwrite($f,$count);
    fclose($f);
?>
