<?php
    
    if (isset($_POST['id']) && isset($_POST['quality']) && isset($_COOKIE['user']))
    {
        $id = $_POST['id'];
        $quality = $_POST['quality'];
        
        require 'connection.php';
        
        if (!isset($_POST['table_name']))
        {
            $link -> query("SET CHARSET utf8;");
        
            $sql = "SELECT * FROM `items` WHERE id = '".$id."'";

            $result = mysqli_query($link, $sql);

            $items = $result -> fetch_array();
            
            $id = $items['item_id'];
            
            $table_name = $items['table_name'];
        }
        else
        {
            $table_name = $_POST['table_name'];
        }
        
        $link -> query("SET CHARSET utf8;");
        
        $sql = "SELECT * FROM `accounts`";

        $result = mysqli_query($link, $sql);
                            
        $massiv[] = array();
        
        while ($data = mysqli_fetch_array($result))
        {
            $massiv = $data;
            
            if (password_verify($massiv['id'], $_COOKIE['user']))
            {
                $sql = "SELECT * FROM `items` WHERE item_id = ".$id." AND table_name = '".$table_name."'";
                $result = mysqli_query($link, $sql);
                $item = $result -> fetch_array();
                
                $sql = "SELECT * FROM `basket` WHERE account_id = ".$massiv['id']." AND item_id = '".$item['id']."'";
                $result = mysqli_query($link, $sql);
                $exist = $result -> fetch_array();
                
                $count = 0;

                if (isset($exist))
                {
                    $count = count($exist);
                }

                if ($count != 0)
                {
                    $quality += $exist['quality'];
                    
                    if ($quality <= 0)
                    {
                        $sql = "DELETE FROM `basket` WHERE account_id = '".$massiv['id']."' AND item_id = (SELECT `items`.`id` FROM `items`, (SELECT * FROM `basket`) as `basket` WHERE `items`.`id` = `basket`.`item_id` AND `basket`.`item_id` = '".$item['id']."' AND `basket`.`account_id` = ".$massiv['id'].")";
                        
                        goto close;
                    }
                    
                    $sql = "UPDATE `basket` SET quality = '".$quality."' WHERE account_id = ".$massiv['id']." AND item_id = (SELECT `items`.`id` FROM `items`, (SELECT * FROM `basket`) as `basket` WHERE `items`.`id` = `basket`.`item_id` AND `basket`.`item_id` = '".$item['id']."' AND `basket`.`account_id` = ".$massiv['id'].")";
                    
                    goto close;
                }
                
                $sql = "INSERT INTO basket VALUES (null, '".$massiv['id']."', '".$item['id']."', '".$quality."')";
            
                close:
                
                mysqli_query($link, $sql);
                
                if ($quality > 0 && !isset($_POST['change']))
                {
                    echo 'Товар добавлен в корзину.';
                }
                else if ($quality <= 0)
                {
                    echo 'Товар удален.';
                }
                
                exit();
            }
        }
        
        echo 'Произошла какая-то ошибка. Пожалуйста, перезайдите на сайт.';
        exit();
    }
    else if (!isset($_COOKIE['user']) && isset($_POST['id']) && isset($_POST['table_name']) && isset($_POST['quality']))
    {
        echo "Неавторизованные пользователи не могут приобретать товары. Пожалуйста, зарегистируйтесь или войдите под своим аккаунтом. Это не сложно :)\nпс, вы получите 100 бонусов совершенно бесплатно за первую регистрацию!";
        exit();
    }

    if (isset($_POST['BasketCounter']))
    {
        require 'connection.php';
        
        $link -> query("SET CHARSET utf8;");

        $sql = "SELECT * FROM `accounts`";

        $result = mysqli_query($link, $sql);

        $massiv[] = array();

        while ($data = mysqli_fetch_array($result))
        {
            $massiv = $data;

            if (password_verify($massiv['id'], $_COOKIE['user']))
            {
                $sql = "SELECT SUM(`quality`) AS 'sum' FROM `basket` WHERE `account_id` = '".$massiv['id']."'";

                $result = mysqli_query($link, $sql);
                
                $count = $result -> fetch_array();
                
                echo $count['sum'];

                exit();
            }
        }
    }

    if (isset($_POST['BuyAllBasket']))
    {
        require 'connection.php';
        
        $link -> query("SET CHARSET utf8;");

        $sql = "SELECT * FROM `accounts`";

        $result = mysqli_query($link, $sql);

        $massiv[] = array();

        while ($data = mysqli_fetch_array($result))
        {
            $massiv = $data;

            if (password_verify($massiv['id'], $_COOKIE['user']))
            {
                $sql = "DELETE FROM `basket` WHERE `account_id` = '".$massiv['id']."'";

                mysqli_query($link, $sql);

                exit();
            }
        }
    }
?>