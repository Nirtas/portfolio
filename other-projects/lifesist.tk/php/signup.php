<?php
    if (isset($_POST['FIO']) && isset($_POST['email']) && isset($_POST['password']))
    {
        $FIO_account = $_POST['FIO'];
        $email_account = $_POST['email'];
        $password_account = $_POST['password'];
        
        if (mb_strlen($FIO_account) < 5 || mb_strlen($FIO_account) > 100)
        {
            echo 'Недопустимая длина ФИО!';
            exit();
        }
        else if (mb_strlen($email_account) < 5 || mb_strlen($email_account) > 50)
        {
            echo 'Недопустимая длина электронной почты!';
            exit();
        }
        else if (mb_strlen($password_account) < 6 || mb_strlen($password_account) > 20)
        {
            echo 'Недопустимая длина пароля!';
            exit();
        }
    
        $password_account = password_hash($password_account, PASSWORD_DEFAULT);

        require_once 'connection.php';
        
        $link -> query("SET CHARSET utf8;");

        $sql = "SELECT * FROM `accounts` WHERE `email` = '$email_account'";

        $result = mysqli_query($link, $sql);

        $user = $result -> fetch_assoc();

        $count = 0;

        if (isset($user))
        {
            $count = count($user);
        }

        if ($count != 0)
        {
            echo 'Пользователь с такой электронной почтой уже зарегистрирован!';
            goto close;
        }

        $sql = "INSERT INTO accounts (FIO, email, password) VALUES ('$FIO_account', '$email_account', '$password_account')";

        mysqli_query($link, $sql);
        
        $sql = "SELECT * FROM `accounts` WHERE `email` = '$email_account'";

        $result = mysqli_query($link, $sql);

        $user = $result -> fetch_assoc();
        
        setcookie('user', password_hash($user['id'], PASSWORD_DEFAULT), time() + 3600*24*90, "/");

        close:

        $link -> close();
    }
?>