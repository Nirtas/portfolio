<?php
    require 'connection.php';

    #HEADER
    
    if ($_POST['key'] == 'header')
    {
        echo ("
            <link rel='stylesheet' href='/css/dropdown.css'>
            <link rel='stylesheet' href='/css/popup.css'>
            <link rel='stylesheet' href='/css/back-to-top.css'>
            <script src='/js/icons.js'></script>
            <script src='/js/jquery.min.js'></script>
            <script src='/js/popup.js'></script>
            <script src='/js/back-to-top.js'></script>
            <script src='/js/jquery.cookie.js'></script>
            <script src='/js/change-basket.js'></script>
            <section class='top_of_header'>
                <section class='logo'>
                    <a href='/index.html'><img src='/images/logo.png'></a>
                </section>

                <section class='header'>
                    <a href=''>Санкт-Петербург</a>
                </section>

                <section class='header'>
                    <a href='tel:+7(800)555-35-35'>8(800)555-35-35</a> (круглосуточно)
                </section>

                <section class='header'>
                    <a href='/ship-payment.html'>Доставка и оплата</a>
                </section>

                <section class='header'>
                    <a href=''>Акции</a>
                </section>
            </section>

                <div class='menu'>
                
                    <ul id='my-drop-down-menu'>
                        <li class='list' style='background-color: #65CB5B; color: #FFFFFF;'><a href='/catalog.html' class='link'>Каталог товаров</a>
                            <ul class='child'>
                                <li><a href='/prigotovleniye-kofe.html' class='link'>Приготовление кофе</a></li>
                                <li><a href='/kholodilniki.html' class='link'>Холодильники</a></li>
                                <li><a href='/mikrovolnovyye-pechi.html' class='link'>Микроволновые печи</a></li>
                                <li><a href='#' class='link'>Варочные поверхности</a></li>
                                <li><a href='#' class='link'>Климатическая техника</a></li>
                                <li><a href='#' class='link'>Мелкая техника для кухни</a></li>
                                <li><a href='#' class='link'>Посудомоечные машины</a></li>
                                <li><a href='#' class='link'>Плиты</a></li>
                                <li><a href='#' class='link'>Вытяжки</a></li>
                                <li><a href='#' class='link'>Стиральные машины</a></li>
                                <li><a href='#' class='link'>Пылесосы</a></li>
                                <li><a href='#' class='link'>Духовые шкафы</a></li>
                                <li><a href='#' class='link'>Мелкая бытовая техника</a></li>
                            </ul>
                        </li>
                    </ul>
                
                    <span class='indent-elements'>
                        <div>
                            <i class='fa fa-search' aria-hidden='true'>
                            </i>
                            <input type='search' class='search' placeholder='Введите Ваш поисковый запрос...'>
                            <input type='button' class='search-button' value='Найти'>
                        </div>
                    </span>
                    ");
                        if ($_COOKIE['user'])
                        {
                            
                            
        
        $link -> query("SET CHARSET utf8;");

        $sql = "SELECT * FROM `accounts`";

        $result = mysqli_query($link, $sql);
                            
        $massiv[] = array();
        
        while ($data = mysqli_fetch_array($result))
        {
            $massiv = $data;
            
            if (password_verify($massiv['id'], $_COOKIE['user']))
            {
                $sql = "SELECT * FROM `accounts` WHERE id = " .$massiv['id']."";
                $result = mysqli_query($link, $sql);
                $user = $result -> fetch_array();
                break;
            }
        }                            
                        echo ("    
                    <span class='indent-elements'>
                        <ul id='my-drop-down-menu'>
                            <li class='list' style='width: 25px; height: 25px; padding: 0 5px 0 0;'><img src='/images/avatars/".$user['icon']."' style='width: 30px; height: 30px; border-radius: 50%;'>
                                <ul class='child'>
                                    <li><a class='btn trigger link' href='#'>Настройки</a></li>
                                    <li><a class='link logout' href='#'>Выйти</a></li>
                                    
<script type = 'text/javascript' >
    $(document).ready(function () {
        $('.logout').on('click', function () {
            $.removeCookie('user', {path: '/'});
            location.reload();
            return false;
        });
    });
</script>
                                </ul>
                            </li>
                        </ul>
                        
                    <div class='modal-wrapper'>
                      <div class='modal'>
                        <div class='head'>
                            <h1>Настройки</h1>
                          <a class='btn-close trigger' href='#'>
                            <i class='fa fa-times' aria-hidden='true'></i>
                          </a>
                        </div>
                        <div class='content-popup'>
                            <div class='icon-FIO'>
                              <img src='/images/avatars/".$user['icon']."'>
                              <h1>Здравствуйте,</h1><h2>".$user['FIO']."</h2>
                            </div>
                            <div class='change-user-avatar'>
                                Изменить аватарку: 
                                <form action='/php/avatar.php' method='post' enctype='multipart/form-data'>
      <input type='file' name='avatar'>
      <input type='submit' name='new-avatar' value='Загрузить файл!'>
    </form>
    ");
    
    $flag = false;
                            
    if (isset($_COOKIE['pic']))
    {
        $name = $_COOKIE['pic'];
        $sql = "UPDATE `accounts` SET icon = '".$name."' WHERE id = ".$user['id']."";
        mysqli_query($link, $sql);
        echo ("
            <script type = 'text/javascript'>
                $.removeCookie('pic', {path: '/'});
                location.reload();
            </script>
        ");
        $flag = true;
    }
    
    if (isset($_COOKIE['error']))
    {
        $error = $_COOKIE['error'];
        echo "<strong>".$error."</strong>";
        echo ("
            <script type = 'text/javascript'>
                $.removeCookie('error', {path: '/'});
            </script>
        ");
    }
                            
    if ($flag)
    {
        echo ("
            <script type = 'text/javascript'>
                location.reload();
            </script>
        ");
        $flag = false;
    }
    
                            echo ("
                            </div>
                            
                            <p class='user-bonuses'>Ваши бонусы: ".$user['bonuses']."</p>
                        </div>
                      </div>
                    </div> 
                        
                    </span>
                    ");
                        }
                        else
                        {
                        echo ("
                       
                    
                    <span class='indent-elements'>
                        <a class='btn trigger' href='#'><img src='/images/profile.png' title='Вход и регистрация'></a>
                    </span>
                    
                    <div class='modal-wrapper'>
                      <div class='modal'>
                        <div class='head'>
                            <h1>Вход/регистрация</h1>
                          <a class='btn-close trigger' href='#'>
                            <i class='fa fa-times' aria-hidden='true'></i>
                          </a>
                        </div>
                        <div class='content-popup'>
                            <?php session_start(); ?>


    <link rel='stylesheet' href='/css/auth.css'>
    <script src='/js/auth.js'></script>

    <div class='container'>
        <div class='frame'>
            <div class='nav'>
                <ul class='links'>
                    <li class='signin-active'><a class='btn-auth'>Вход</a></li>
                    <li class='signup-inactive'><a class='btn-auth'>Регистрация</a></li>
                </ul>
            </div>
            <div ng-app ng-init='checked = false'>
                <form class='form-signin' action='/php/signin.php' method='post'>
                    <label class='labels' for='email'>Email</label>
                    <input class='form-styling' type='text' name='email'>
                    <label class='labels' for='password'>Пароль</label>
                    <input class='form-styling' type='password' name='password'>
                    <input type='submit' class='btn-signin' value='Войти'>
                </form>

                <form class='form-signup' action='/php/signup.php' method='post'>
                    <label class='labels' for='FIO'>ФИО (до 100 символов)</label>
                    <input class='form-styling' type='text' name='FIO'>
                    <label class='labels' for='email'>Email (до 50 символов)</label>
                    <input class='form-styling' type='text' name='email'>
                    <label class='labels' for='password'>Пароль (от 6 до 20 символов)</label>
                    <input class='form-styling' type='password' name='password'>
                    <input type='submit' id='auth' class='btn-signup' value='Зарегистрироваться'>
                </form>
            </div>
            <div class='forgot'>
                <a href='#'>Забыли пароль?</a>
            </div>
        </div>
    </div>
    
                        </div>
                      </div>
                    </div> 
                        ");
                        }
        
                    echo ("
                    <span class='indent-elements'>
                        <span class='basket-indent-text'>
                            <a href='/php/basket.php'><img src='/images/basket.png' title='Корзина'></a>
                            ");
        
                            if ($_SERVER['REQUEST_URI'] != '/php/basket.php')
                            {
                                echo "<span class='BasketCounter'>0</span>";
                            }
        
                            echo ("
                        </span>
                    </span>
                </div>
                <hr>
            ");
    }

    
        
    

    #FOOTER

    if ($_POST['key'] == 'footer')
    {
        echo ("
            <div style='background: #65CB5B; color: #FFFFFF; display: flex; flex-wrap: wrap; justify-content: center;'>
                <span class='footer'>
                    <div class='footer-block'>
                        <img src='/images/logo.png'>
                        <p></p>
                        <a href='tel:+7(800)555-35-35'>8(800)555-35-35</a> (круглосуточно)
                    </div>
                    <div class='footer-block'>
                        <a href=''>Адреса магазинов</a> <br>
                        <a href='/company.html'>О компании</a> <br>
                        <a href=''>Новости</a> <br>
                        <a href=''>Соискателям</a> <br>
                        <a href=''>Партнерам</a> <br>
                    </div>
                    <div class='footer-block'>
                        <a href='/ship-payment.html'>Доставка и оплата</a> <br>
                        <a href=''>Акции</a> <br>
                        <a href=''>Услуги</a> <br>
                        <a href=''>Пресса</a> <br>
                        <a href=''>Помощь</a> <br>
                    </div>
                </span>
            </div>
            <a href='#' class='scrollup'>Наверх</a>
        ");
    }

?>