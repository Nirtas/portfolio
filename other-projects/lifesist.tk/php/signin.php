<?php
    if (isset($_POST['email']) && isset($_POST['password']))
    {
        $email_account = $_POST['email'];
        $password_account = $_POST['password'];
        
        if (mb_strlen($email_account) < 5 || mb_strlen($email_account) > 50)
        {
            echo 'Недопустимая длина электронной почты!';
            exit();
        }
        else if (mb_strlen($password_account) < 6 || mb_strlen($password_account) > 20)
        {
            echo 'Недопустимая длина пароля!';
            exit();
        }
        
        require_once 'connection.php';

        $link -> query("SET CHARSET utf8;");

        $sql = "SELECT * FROM `accounts` WHERE `email` = '$email_account'";

        $result = mysqli_query($link, $sql);

        $user = $result -> fetch_assoc();

        if (!password_verify($password_account, $user['password']))
        {
            echo 'Пользователь с такими данными не найден!';
            goto close;
        }
        
        setcookie('user', password_hash($user['id'], PASSWORD_DEFAULT), time() + 3600*24*90, "/");
        
        close:
        
        $link -> close();
    }
?>