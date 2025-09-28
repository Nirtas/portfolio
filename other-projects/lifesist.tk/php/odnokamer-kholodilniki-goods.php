<?php
    
    require 'connection.php';
    
    #MySQL
    
    $uri = explode('/', $_SERVER['PHP_SELF'])[2];
    $uri = substr ($uri, 0, strrpos($uri, '.'));
    
    //$link = mysqli_connect($host, $user, $password, $database);
    
    $link -> query("SET CHARSET utf8");
    $sql = "SELECT * FROM `".$uri."`";
    $result = mysqli_query($link, $sql);
    $array = array();

    while($data = mysqli_fetch_array($result))
    {
        $array[] = $data;
    }
    
    $id = $_GET['id'] - 1;

    echo ("
            <!DOCTYPE html>
            <html lang='ru'>

            <head>
                <meta charset='UTF-8'>
                <link rel='icon' type='image/png' sizes='16x16' href='/favicon-16x16.png'>
                <link href='/css/global.css' rel='stylesheet'>
                <link href='/css/item.css' rel='stylesheet'>
                <title>".$array[$id]['name']."</title>
                <script src='/js/icons.js'></script>
                <script src='/js/jquery.min.js'></script>
                <script src='/js/countItems.js'></script>
                <script src='/js/radiobuttons.js'></script>
                <script src='/js/review.js'></script>
            </head>

            <body>
                ");
                    $_POST['key'] = 'header';
                    include ('header-footer.php');
            echo (" 

                <main>
                    <p class='indent'></p>
                    <form action='' name='item".($id+1)."'>
                        <div class='item'>
                            <div class='item-img'>
                                <img src='/images/".$array[$id]['image-large']."' alt=''>
                                <p>
                                    Цена указана без учета персональных скидок. Точную информацию по наличию товара вы можете уточнить у менеджера по телефону:
                                </p>
                                <h3>8(800)555-35-35</h3>
                            </div>
                            <div class='item-name-price'>
                                <div class='item-name'>
                                    ".$array[$id]['name']."
                                </div>
                                <div class='item-price'>
                                    <div class='price'>
                                        ".number_format($array[$id]['price'], 0, '.', ' ')." руб.
                                        <p></p>
                                        <div>
                                            <span class='minus'><button name='minus'>-</button></span>
                                            <input type='text' value=1 id='countItem' readonly>
                                            <span class='plus'><button name='plus'>+</button></span>
                                        </div>
                                        <input type='button' data-id='".($id+1)."' data-name='".$uri."' class='buy' value='В корзину'>
                                    </div>
                                    <div class='bonuses'>
                                        ".number_format(ceil($array[$id]['price']*0.04), 0, '.', ' ')." бонусов
                                        <br>
                                        <a href=''>Бонусная программа</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                    <div style='padding-left: 15px;'>
                        <div class='radio_buttons'>
                            <div>
                                <input class='radio_option' type='radio' name='option' id='butSpecs' checked>
                                <label for='butSpecs'>Характеристики</label>
                            </div>
                            <span class='indent-elements'>
                                <div>
                                    <input class='radio_option' type='radio' name='option' id='butDescription'>
                                    <label for='butDescription'>Описание</label>
                                </div>
                            </span>
                            <span class='indent-elements'>
                                <div>
                                    <input class='radio_option' type='radio' name='option' id='butReviews'>
                                    <label for='butReviews'>Отзывы</label>
                                </div>
                            </span>
                        </div>
                        <br>
                        <div id='specs'>
                            <table>
                                <tr>
                                    <td>Наименование</td>
                                    <td>".$array[$id]['name']."</td>
                                </tr>
                                <tr>
                                    <td>Марка</td>
                                    <td>".$array[$id]['brand']."</td>
                                </tr>
                                <tr>
                                    <td>Объем холодильной камеры</td>
                                    <td>".$array[$id]['coolingVolume']." л</td>
                                </tr>
                                <tr>
                                    <td>Объем морозильной камеры</td>
                                    <td>".$array[$id]['freezerVolume']." л</td>
                                </tr>
                                <tr>
                                    <td>Тип разморозки ХК</td>
                                    <td>".$array[$id]['typeDefrosCooling']."</td>
                                </tr>
                                <tr>
                                    <td>Тип разморозки МК</td>
                                    <td>".$array[$id]['typeDefrosFreezer']."</td>
                                </tr>
                                <tr>
                                    <td>Тип управления</td>
                                    <td>".$array[$id]['controlType']."</td>
                                </tr>
                                <tr>
                                    <td>Энергопотребление (в год)</td>
                                    <td>".$array[$id]['energyConsum']."</td>
                                </tr>
                                <tr>
                                    <td>Уровень шума</td>
                                    <td>".$array[$id]['noiseLevel']."</td>
                                </tr>
                                <tr>
                                    <td>Цвет</td>
                                    <td>".$array[$id]['color']."</td>
                                </tr>
                                <tr>
                                    <td>Размеры (ВхШхГ), см</td>
                                    <td>".$array[$id]['sizes']."</td>
                                </tr>
                                <tr>
                                    <td>Вес, кг</td>
                                    <td>".$array[$id]['weight']." кг</td>
                                </tr>
                                <tr>
                                    <td>Гарантия</td>
                                    <td>".$array[$id]['warranty']." месяцев</td>
                                </tr>
                                <tr>
                                    <td>Страна</td>
                                    <td>".$array[$id]['country']."</td>
                                </tr>
                            </table>
                        </div>

                        <div id='description' style='display: none;'>
                            ".$array[$id]['description']."
                        </div>

                       <div id='reviews' style='display: none;'>
                         ");


        if (isset($_COOKIE['user']))
        {
            echo ("
            <form action='/php/newReview.php' method='post' class='add-review'>
                <div>
                    <div style='display: flex; justify-content: center; margin-top: 10px;'>
                        <div>
                            Достоинства:<br>
                            <textarea style='width: 333px; height: 100px; border-radius: 20px; text-indent: 10px; resize: none; margin-right: 30px;' maxlength='300' id='pros'></textarea>
                        </div>
                        <div>
                            Недостатки:<br>
                            <textarea style='width: 333px; height: 100px; border-radius: 20px; text-indent: 10px; resize: none;' maxlength='300' id='cons'></textarea>
                        </div>
                    </div>
                    <div style='display: flex; justify-content: center; margin-top: 10px;'>
                        <div>
                            Комментарий:<br>
                            <textarea style='width: 700px; height: 100px; border-radius: 20px; text-indent: 10px; resize: none;' maxlength='1000' id='commentary'></textarea>
                        </div>
                    </div>
                    <div style='display: flex; justify-content: center; margin-top: 10px; margin-bottom: 30px;'>
                        <div style='margin-right: 30px;'>
                            Оцените товар:
                            <div class='review_stars_wrap'>
                                <div id='review_stars'>
                                    <input id='star-4' type='radio' name='stars'>
                                    <label title='Отлично' for='star-4'>
                                        <i class='fas fa-star'></i>
                                    </label>
                                    <input id='star-3' type='radio' name='stars'>
                                    <label title='Хорошо' for='star-3'>
                                        <i class='fas fa-star'></i>
                                    </label>
                                    <input id='star-2' type='radio' name='stars' checked='checked'>
                                    <label title='Нормально' for='star-2'>
                                        <i class='fas fa-star'></i>
                                    </label>
                                    <input id='star-1' type='radio' name='stars'>
                                    <label title='Плохо' for='star-1'>
                                        <i class='fas fa-star'></i>
                                    </label>
                                    <input id='star-0' type='radio' name='stars'>
                                    <label title='Ужасно' for='star-0'>
                                        <i class='fas fa-star'></i>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div>
                            <input type='submit' id='butAddReview' value='Добавить отзыв'>
                        </div>
                    </div>
                </div>
            </form>

            ");
        }
        else
        {
            echo "<p>Зарегистрируйтесь, чтобы добавить отзыв.</p>";
        }

    $link -> query("SET CHARSET utf8");

    $sql = "SELECT * FROM `reviews` WHERE item_id = (SELECT id FROM `items` WHERE item_id = '".($id+1)."' AND table_name = '".$uri."')";

    $result = mysqli_query($link, $sql);
            
    $reviews[] = array();

    if (mysqli_num_rows($result) != 0)
    {
        while ($reviews_info = mysqli_fetch_array($result))
        {
            $reviews = $reviews_info;
            
            $sql = "SELECT * FROM `accounts` WHERE `accounts`.id = '".$reviews['account_id']."'";
            
            $res = mysqli_query($link, $sql);
            
            $massiv[] = array();
            
            while ($data = mysqli_fetch_array($res))
            {
                $massiv = $data;
                
                echo "<b>Отзывы о товаре:</b>";
                
                echo ("
    <div class='review'>
        <div class='review-card'>
            <div class='img-review-card'>
                <img src='/images/avatars/".$massiv['icon']."' alt=''>
                <br>
                <b>
                    <div class='review_stars_wrap' style='display: flex; justify-content: center;'>
                        <div id='review_stars'>
                            <input id='star-r".$reviews['account_id']."4' type='radio' name='stars-".$reviews['id']."' disabled>
                            <label title='Отлично' for='star-r".$reviews['account_id']."4'>
                                <i class='fas fa-star'></i>
                            </label>
                            <input id='star-r".$reviews['account_id']."3' type='radio' name='stars-".$reviews['id']."' disabled>
                            <label title='Хорошо' for='star-r".$reviews['account_id']."3'>
                                <i class='fas fa-star'></i>
                            </label>
                            <input id='star-r".$reviews['account_id']."2' type='radio' name='stars-".$reviews['id']."' disabled>
                            <label title='Нормально' for='star-r".$reviews['account_id']."2'>
                                <i class='fas fa-star'></i>
                            </label>
                            <input id='star-r".$reviews['account_id']."1' type='radio' name='stars-".$reviews['id']."' disabled>
                            <label title='Плохо' for='star-r".$reviews['account_id']."1'>
                                <i class='fas fa-star'></i>
                            </label>
                            <input id='star-r".$reviews['account_id']."0' type='radio' name='stars-".$reviews['id']."' disabled>
                            <label title='Ужасно' for='star-r".$reviews['account_id']."0'>
                                <i class='fas fa-star'></i>
                            </label>
                        </div>
                    </div>
                    
        <script type='text/javascript'>
             const links = $('input[name=stars-".$reviews['id']."]');
    
            $.each(links, function () {
                if ($(this).attr('id') == 'star-r".$reviews['account_id'].($reviews['rating']-1)."')
                {
                    $(this).prop('checked', true);
                }
                else
                {
                    $(this).prop('checked', false);
                }
            });
        </script>
                    
                    ".$massiv['FIO']."
                    <br>
                    ".$reviews['date']."
                    <p></p>
                </b>
            </div>
            <div class='name-review-card'>
                <div class='pros'>
                    <strong>Достоинства:</strong>
                    ".$reviews['pros']."
                </div>
                <div class='cons'>
                    <strong>Недостатки:</strong>
                    ".$reviews['cons']."
                </div>
                <div class='commentary'>
                    <strong>Комментарий:</strong>
                    ".$reviews['commentary']."
                </div>
            </div>
        </div>
    </div>
            ");
            }


            
        }
    }
    else
    {
        print_r('Отзывов нету.');
    }

                    echo ("
                        </div>
                    </div>
                </main>

                <p class='indent'></p>
                <p class='indent'></p>
                ");
                    $_POST['key'] = 'footer';
                    include ('header-footer.php');
            echo ("        
            </body></html>
    ");

    $link -> close();
?>