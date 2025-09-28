from tkinter import Label, Button, Toplevel, Entry, messagebox
import main_menu


# Класс окна ввода информации пользователя
class InputInformation:
    def __init__(self, master, result):
        self.input_information = Toplevel(master)
        self.input_information.configure(background='#2c386a')
        self.input_information.title('Обучающее приложение: Астрономия')
        self.input_information.geometry('500x500')
        self.input_information.resizable(False, False)

        Label(self.input_information,
              text='Введите свои данные',
              font='sans-serif 20',
              bg='#2c386a',
              fg='white').grid(row=1, column=1, columnspan=2)

        Label(self.input_information,
              text='Имя',
              font='sans-serif 14',
              bg='#2c386a',
              fg='white').grid(row=2, column=1, sticky='w')

        self.entry_firstname = Entry(self.input_information,
                                     font='sans-serif 14')

        self.entry_firstname.grid(row=2, column=2)

        Label(self.input_information,
              text='Фамилия',
              font='sans-serif 14',
              bg='#2c386a',
              fg='white').grid(row=3, column=1, sticky='w')

        self.entry_surname = Entry(self.input_information,
                                   font='sans-serif 14')

        self.entry_surname.grid(row=3, column=2)

        Label(self.input_information,
              text='Группа',
              font='sans-serif 14',
              bg='#2c386a',
              fg='white').grid(row=4, column=1, sticky='w')

        self.entry_group = Entry(self.input_information,
                                 font='sans-serif 14')

        self.entry_group.grid(row=4, column=2)

        self.but1 = Button(self.input_information,
                           text="Отправить",
                           cursor='hand2',
                           font='sans-serif 16',
                           command=lambda: self.save_result(result),
                           bd=1,
                           bg='white')

        self.but1.grid(row=5, column=1, columnspan=2)

        self.input_information.grid_columnconfigure(0, minsize=75)
        self.input_information.grid_columnconfigure(1, minsize=105)
        self.input_information.grid_columnconfigure(2, minsize=105)
        self.input_information.grid_columnconfigure(3, minsize=75)

        self.input_information.grid_rowconfigure(1, minsize=145)
        self.input_information.grid_rowconfigure(2, minsize=55)
        self.input_information.grid_rowconfigure(3, minsize=55)
        self.input_information.grid_rowconfigure(4, minsize=55)
        self.input_information.grid_rowconfigure(5, minsize=245)

        self.input_information.protocol("WM_DELETE_WINDOW", self.exit)
        self.input_information.iconbitmap('icon.ico')
        self.input_information.mainloop()

    # Заключительное формирование результата и запись в файл
    def save_result(self, result):
        # Проверка незаполненных полей
        if (self.entry_firstname.get() == '') or (self.entry_surname.get() == '') or (self.entry_group.get() == ''):
            messagebox.showinfo('Ошибка', 'Введите данные')
            return

        result[1] = self.entry_firstname.get()
        result[2] = self.entry_surname.get()
        result[3] = self.entry_group.get()

        # Открытие файла результатов тестирований и запись в него нового результата
        file = open('./files/Результаты тестирований.csv', 'a')
        file.write('\n' + ','.join(result))
        file.close()

        # Вывод результата тестирования на экран в виде сообщения
        messagebox.showinfo('Результат тестирования',
                            f'Процент правильных ответов в теме "{result[4]}": {result[5]}. '
                            f'Ваша оценка: {result[6]}')
        # Открытие главного меню
        self.open_main_menu()

    # Открытие главного меню
    def open_main_menu(self):
        self.input_information.withdraw()
        main_menu.MainMenu(self.input_information)
        self.input_information.deiconify()

    # Событие при закрытии окна
    def exit(self):
        raise SystemExit
