from tkinter import messagebox, Label, Button, PhotoImage, Tk, Toplevel, Frame
import os
import theory


# Класс окна списка тем
class Themes:
    def __init__(self, master):
        self.themes = Toplevel(master)
        self.themes.configure(background='#2c386a')
        self.themes.title('Обучающее приложение: Астрономия')
        self.themes.geometry('800x700')
        self.themes.resizable(False, False)
        self.theme_file = {}
        self.frame_theme = Frame(self.themes, bg='#2c386a')
        self.frame_theme.grid(row=1, column=0, columnspan=10, rowspan=3)

        Label(self.themes,
              text='Темы',
              font='sans-serif 20',
              bg='#2c386a',
              fg='white').grid(row=0, column=4)

        but1 = Button(self.themes,
                      text="Назад",
                      cursor='hand2',
                      font='sans-serif 16',
                      bd=1,
                      bg='white')

        # Присваивание кнопке события "Назад"
        but1.bind('<Button-1>', self.but_back)
        but1.grid(row=5, column=0)

        self.themes.grid_rowconfigure(0, minsize=120)
        self.themes.grid_rowconfigure(5, minsize=450)

        self.themes.protocol("WM_DELETE_WINDOW", self.exit)
        self.themes.iconbitmap('icon.ico')
        self.read_themes()
        self.themes.mainloop()

    # Создание блока темы
    def create_theme_block(self, photo, theme_name, i):
        row = 1
        # Переход на новую строку
        if i >= 4:
            i -= 4
            row = 3
            self.themes.grid_rowconfigure(5, minsize=450)
        else:
            self.themes.grid_rowconfigure(5, minsize=750)

        label = Label(self.frame_theme,
                      text=theme_name,
                      image=photo,
                      cursor='hand2',
                      width=170,
                      bd=0,
                      bg='#2c386a',
                      fg='white')

        label.image = photo

        padx_w = 0

        if i % 4 == 0:
            padx_w = 20

        label.bind('<Button-1>', self.open_theme)
        label.grid(row=row, column=1 + i, sticky='wens', padx=(padx_w, 20))

        but = Button(self.frame_theme,
                     text=theme_name,
                     bd=1,
                     cursor='hand2',
                     font='sans-serif 12',
                     wraplength=170,
                     bg='white')

        but.bind('<Button-1>', self.open_theme)
        but.grid(row=row + 1, column=1 + i, sticky='wen', padx=(padx_w, 20), pady=(0, 20))

    # Чтение информации о темах из файлов
    def read_themes(self):
        list = os.listdir('./files/themes')
        count_themes = len(list)
        # Проверка на пустоту каталога с файлами тем
        if count_themes < 2:
            messagebox.showerror('Ошибка', 'Тем должно быть минимум 2')
            raise SystemExit
        i = 0
        while i < count_themes:
            # Открытие файлов по очереди
            file = open('./files/themes/' + list[i], 'r', -1, 'utf-8')
            theme_content = file.read()

            theme_name = ''
            # Поиск в файле строки "Название темы: "
            index = theme_content.find('Название темы: ')
            if index != -1:
                index += len('Название темы: ')
                # Запись строки из файла в переменную названия темы
                while theme_content[index] != '\n':
                    theme_name += theme_content[index]
                    index += 1

            pic_name = ''
            # Поиск в файле строки "Картинка темы: "
            index = theme_content.find('Картинка темы: ')
            if index != -1:
                index += len('Картинка темы: ')
                # Запись строки из файла в переменную квартинки темы
                while theme_content[index] != '\n':
                    pic_name += theme_content[index]
                    index += 1

            # Создание блока темы, если картинка и название существуют
            if theme_name != '' and os.path.exists('./images/' + pic_name):
                self.theme_file[theme_name] = file.name
                theme_pic = PhotoImage(file='./images/' + pic_name)
                self.create_theme_block(theme_pic, theme_name, i)
            file.close()
            i += 1

    # Открытие выбранной темы
    def open_theme(self, event):
        self.themes.withdraw()
        theory.Theory(self.themes, self.theme_file.get(event.widget.cget('text')))
        self.themes.deiconify()

    # Назад в главное меню
    def but_back(self, event):
        self.themes.quit()
        self.themes.destroy()

    # Событие при закрытии окна
    def exit(self):
        raise SystemExit


# Открытие окна главного меню, если этот файл был запущен отдельно
if __name__ == '__main__':
    root = Tk()
    root.withdraw()
    Themes(root)
