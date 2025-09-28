<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="UTF-8">
    <link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
    <link href="/css/global.css" rel="stylesheet">
    <link href="/css/basket.css" rel="stylesheet">
    <title>Корзина</title>
    <script src='/js/icons.js'></script>
    <script src='/js/jquery.min.js'></script>
    <script src="/js/countItems.js"></script>
</head>

<body>
    <?php
        $_POST['key'] = 'header';
        include ('header-footer.php');
    ?>

    <p class="indent"></p>

    <main>
        
        <?php
        
            if (isset($_COOKIE['user']))
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
                        $sql = "SELECT * FROM `basket` WHERE account_id = '".$massiv['id']."'";

                        $res = mysqli_query($link, $sql);
                        
                        if (mysqli_num_rows($res) != 0)
                        {
                            $mass[] = array();
                            
                            echo ("
                                <h1>Ваша корзина</h1>
                                <p class='indent'></p>
                            ");

                            while ($dat = mysqli_fetch_array($res))
                            {
                                $mass = $dat;

                                $sql = "SELECT * FROM `items` WHERE id = '".$mass['item_id']."'";

                                $re = mysqli_query($link, $sql);

                                $ma[] = array();

                                while ($da = mysqli_fetch_array($re))
                                {
                                    $ma = $da;

                                    $sql = "SELECT * FROM `".$ma['table_name']."` WHERE id = '".$ma['item_id']."'";

                                    $r = mysqli_query($link, $sql);

                                    $m[] = array();
                                    
                                    while ($d = mysqli_fetch_array($r))
                                    {
                                        $m = $d;
                                        
                                        echo ("
                                    <div class='basket-card'>
                                        <div class='img-item-basket'>
                                            <a href='/php/".$ma['table_name'].".php?id=".$m['id']."'>
                                                <img src=/images/".$m['image']." alt = ''>
                                            </a>
                                        </div>
                                        <div class='name-item-basket'>
                                            <a href='/php/".$ma['table_name'].".php?id=".$m['id']."'>
                                                ".$m['name']."
                                            </a>
                                        </div>
                                        <div class='count-item-basket'>
                                            <div class='number'>
                                                <span class='minus'>
                                                    <button name='minus'>-</button>
                                                </span>
                                                <input type='text' value='".$mass['quality']."' data-id='".$ma['id']."' data-name='".$ma['table_name']."' onchange=\"NewPriсe($('.minus').parent().find('input[data-id=".$ma['id']."]'), ".$m['price'].")\" readonly>
                                                <span class='plus'>
                                                    <button name='plus'>+</button>
                                                </span>
                                            </div>
                                        </div>
                                        <div class='price-item-basket' data-id='".$ma['id']."'>".$m['price']*$mass['quality']." руб.
                                        </div>
                                        <div>
                                            <button id='deleteItembasket' onclick=\"ChangeQuality($('.minus').parent().find('input[data-id=".$ma['id']."]'), -100)\">&#10006;</button>
                                        </div>
                                    </div>
                                ");

                                    }
                                }
                            }
                            
                            echo ("
                                <div class='total'>
                                    <span class='total-price'>
                                        0 товаров на сумму 0 руб.
                                    </span>
                                        <button class='buyItems' onclick='BuyAllBasket()'>Купить</button>
                                </div>
                            ");
                            
                        }
                        else
                        {
                            echo ("
                                <div class='basket-warning'>Ваша корзина пуста. Вы можете перейти на <a href='/catalog.html'>страницу каталога товаров</a>.</div>
                            ");
                        }
                        
                        goto close;
                    }

                }
                
                close:

            }
            else
            {
                echo "<div class='basket-warning'>На нашем сайте покупать незарегистрированным пользователям нельзя. Зарегистрируйтесь и получите 100 бонусов, которые можно потратить на первую покупку! &#128521;</div>";
            }
        ?>
        
        <p class="indent"></p>
    </main>

    <p class="indent"></p>
    <p class="indent"></p>
    
    <?php
        $_POST['key'] = 'footer';
        include ('header-footer.php');
    ?>
</body>

</html>
