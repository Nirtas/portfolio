<?php

    $uri = explode('/', $_SERVER['PHP_SELF'])[1];
    $uri = substr ($uri, 0, strrpos($uri, '.'));
    
    $link -> query("SET CHARSET utf8");
    $sql = "SELECT * FROM `".$uri."-goods`";
    $result = mysqli_query($link, $sql);

    while ($row = mysqli_fetch_array($result))
    {
        echo ("
            <li>
                <form action='/php/change-basket.php' name='item".$row['id']."' method='post'>
                    <div class='item-card'>
                        <div class='img-item-card'>
                            <a href=/php/".$uri."-goods.php?id=".$row['id']." name='".$uri."-goods'>
                                <img src='images/".$row['image']."' alt=''>
                            </a>
                        </div>
                        <div class='name-item-card'>
                            <a href=/php/".$uri."-goods.php?id=".$row['id']." name='".$uri."-goods'>
                                ".$row['name']."
                            </a>
                            <br>
                            <h2>
                                <p>Торговая марка: ".$row['brand']."</p>
                                <p>".$row['country']." | ".$row['weight']." кг</p>
                                <p class='availability'>В наличии</p>
                            </h2>
                        </div>
                        <div class='price-item-card'>
                            ".number_format($row['price'], 0, '.', ' ')." руб.
                            <p></p>
                            <div>
                                <span class='minus'><button name='minus'>-</button></span>
                                <input type='text' id='countItem' value='1' readonly>
                                <span class='plus'><button name='plus'>+</button></span>
                            </div>
                            <input type='button' data-id='".$row['id']."' data-name='".$uri."-goods' class='buy' value='В корзину'>
                        </div>
                    </div>
                </form>
            </li>
        ");
    }

    mysqli_close($link);
?>
