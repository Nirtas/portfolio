<?php
    if (isset($_POST['item_id']) && isset($_POST['table_name']) && isset($_POST['pros']) && isset($_POST['cons']) && isset($_POST['commentary']) && isset($_POST['rating']))
    {
        $item_id = $_POST['item_id'];
        $table_name = $_POST['table_name'];
        $pros = $_POST['pros'];
        $cons = $_POST['cons'];
        $commentary = $_POST['commentary'];
        $rating = substr($_POST['rating'], 5) + 1;
        
        if ($commentary == '')
        {
            echo 'Поле "Комментарий" не должно быть пустым!';
            exit();
        }
        
        if ($pros == '')
        {
            $pros = 'Нет.';
        }
        
        if ($cons == '')
        {
            $cons = 'Нет.';
        }
        
        require_once 'connection.php';
        
        $link -> query("SET CHARSET utf8;");

        $sql = "SELECT * FROM `accounts`";

        $result = mysqli_query($link, $sql);

        $massiv[] = array();
        
        while ($data = mysqli_fetch_array($result))
        {
            $massiv = $data;
            
            if (password_verify($massiv['id'], $_COOKIE['user']))
            {
                $sql = "SELECT id FROM `items` WHERE item_id = '".$item_id."' AND table_name = '".$table_name."'";

                $item = mysqli_query($link, $sql) -> fetch_array();
                
                $sql = "SELECT * FROM `reviews` WHERE account_id = '".$massiv['id']."' AND item_id = '".$item['id']."'";

                if (mysqli_num_rows(mysqli_query($link, $sql)) == 0)
                {
                    $sql = "INSERT INTO `reviews` VALUES (null, '".$massiv['id']."', '".$item['id']."', '".$pros."', '".$cons."', '".$commentary."', '".$rating."', '".date("Y.m.d")."')";

                    mysqli_query($link, $sql);
                }
                else
                {
                    echo 'Можно добавить только один отзыв к товару!';
                    
                    goto close;
                }
            }
            
            
        }
        
        close:
        
        $link -> close();
    }
?>