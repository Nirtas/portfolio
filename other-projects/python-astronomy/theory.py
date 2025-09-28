from tkinter import Button, Toplevel, Frame
from tk_html_widgets import HTMLScrolledText
import test


# Класс окна теории темы
class Theory:
    def __init__(self, master, theme_file):
        self.theory = Toplevel(master)
        self.theory.configure(background='#2c386a')
        self.theory.title('Обучающее приложение: Астрономия')
        self.theory.geometry('1000x800')
        self.theory.resizable(False, False)

        self.frame_theory = Frame(self.theory, bg='#2c386a')
        self.frame_theory.grid(row=0, column=0, rowspan=7, columnspan=10, padx=(20, 0), pady=20)

        self.frame_theory.grid_columnconfigure(0, minsize=30)
        self.frame_theory.grid_columnconfigure(1, minsize=112)
        self.frame_theory.grid_columnconfigure(2, minsize=112)
        self.frame_theory.grid_columnconfigure(3, minsize=112)
        self.frame_theory.grid_columnconfigure(4, minsize=112)
        self.frame_theory.grid_columnconfigure(5, minsize=112)
        self.frame_theory.grid_columnconfigure(6, minsize=112)
        self.frame_theory.grid_columnconfigure(7, minsize=112)
        self.frame_theory.grid_columnconfigure(8, minsize=112)
        self.frame_theory.grid_columnconfigure(9, minsize=30)

        self.frame_theory.grid_rowconfigure(0, minsize=100)
        self.frame_theory.grid_rowconfigure(1, minsize=100)
        self.frame_theory.grid_rowconfigure(2, minsize=100)
        self.frame_theory.grid_rowconfigure(3, minsize=100)
        self.frame_theory.grid_rowconfigure(4, minsize=100)
        self.frame_theory.grid_rowconfigure(5, minsize=100)
        self.frame_theory.grid_rowconfigure(6, minsize=100)

        self.theory_file = theme_file

        # Запуск процедуры считывания теории из файла
        self.create_html_theory()

        but1 = Button(self.theory,
                      text="Назад",
                      cursor='hand2',
                      font='sans-serif 16',
                      bd=1,
                      bg='white')

        but1.bind('<Button-1>', self.but_back)
        but1.grid(row=7, column=0)

        but2 = Button(self.theory,
                      text="Пройти тест",
                      cursor='hand2',
                      font='sans-serif 16',
                      bd=1,
                      bg='white')

        but2.bind('<Button-1>', self.open_test)
        but2.grid(row=7, column=4)

        self.theory.protocol("WM_DELETE_WINDOW", self.exit)
        self.theory.iconbitmap('icon.ico')
        self.theory.mainloop()

    # Помещение теории темы в текстовый блок для отображения
    def create_html_theory(self):
        self.theme_name()
        self.show_theory()
        html_theory = HTMLScrolledText(self.frame_theory,
                                       html='<h1 style="text-align: center"> ' +
                                            self.theme_name() + ' </h1> ' + self.html)
        html_theory.grid(row=0, column=0, sticky='wens', columnspan=15, rowspan=10)

    # Возвращение названия текущей темы
    def theme_name(self):
        f = open(self.theory_file, 'r', -1, 'utf-8')
        theme_content = f.read()
        f.close()
        theme_name = ''
        index = theme_content.find('Название темы: ')
        if index != -1:
            index += len('Название темы: ')
            while theme_content[index] != '\n':
                theme_name += theme_content[index]
                index += 1
        return theme_name

    # Событие считывания теории  темы
    def show_theory(self):
        f = open(self.theory_file, 'r', -1, 'utf-8')
        theme_content = f.read()
        f.close()
        index = theme_content.find('Теория:')
        self.html = ''
        if index != -1:
            index += len('Теория:') + 1
            index_end = theme_content.find('Тест:\nВопрос 1:')
            while index != index_end:
                self.html += theme_content[index]
                index += 1

    # Кнопка "Назад"
    def but_back(self, event):
        self.theory.quit()
        self.theory.destroy()

    # Открытие окна теста
    def open_test(self, event):
        self.theory.withdraw()
        test.Test(self.theory, self.theory_file)
        self.theory.deiconify()

    # Событие при закрытии окна
    def exit(self):
        raise SystemExit
