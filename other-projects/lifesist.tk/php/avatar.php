<?php
    if (isset($_POST['new-avatar']))
    {
        $file = $_FILES['avatar'];
        
        $file_name = $file['name'];
        $file_size = $file['size'];
        $file_tmp = $file['tmp_name'];
        $file_ext = strtolower(end(explode('.', $file_name)));
        
        if($file_name == '')
        {
            $error = 'Вы не выбрали файл.';
            goto close;
        }

        if($file_size > 204800)
        {
            $error = 'Максимальный размер файла - 200 КБ.';
            goto close;
        }

        $filename = strtolower(substr($file_name, 0, strripos($file_name, '.')));

        $types = array('jpg', 'png', 'tif', 'tiff', 'bmp', 'jpeg');

        if(!in_array($file_ext, $types))
        {
            $error = 'Недопустимый тип файла.';
            goto close;
        }
        
        $name = md5($filename) . rand(0, 9999) . '.' . $file_ext;
        $name_path = '../images/avatars/' . $name;
        
        if (file_exists($name_path))
        {
            $error = 'Что-то пошло не так. Попробуйте еще раз.';
        }
        else
        {
            copy($file_tmp, $name_path);
        }
        
        close:
        
        echo "<script src='/js/jquery.min.js'></script>";
        echo "<script src='/js/jquery.cookie.js'></script>";
        
        if (isset($error))
        {
            echo "<script type='text/javascript'> $.cookie('error', '".$error."', {path: '/'}); </script>";
        }
        else
        {
            echo "<script type='text/javascript'> $.cookie('pic', '".$name."', {path: '/'}); </script>";
        }
        
        echo "<script type = 'text/javascript'> window.location.href = '".$_SERVER['HTTP_REFERER']."'; </script>";
    }
?>