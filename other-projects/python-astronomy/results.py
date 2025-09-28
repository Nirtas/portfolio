from tkinter import Label, Button, Toplevel, Frame, END
from tkinter.ttk import Treeview
import csv


# Класс окна результатов тестирований
class Results:
    def __init__(self, master):
        self.results = Toplevel(master)
        self.results.configure(background='#2c386a')
        self.results.title('Обучающее приложение: Астрономия')
        self.results.geometry('900x600')
        self.results.resizable(False, False)

        self.frame_results = Frame(self.results, bg='#2c386a')
        self.frame_results.grid(row=0, column=0, columnspan=5, rowspan=3)

        self.frame_results.grid_columnconfigure(0, minsize=30)
        self.frame_results.grid_columnconfigure(1, minsize=225)
        self.frame_results.grid_columnconfigure(2, minsize=100)
        self.frame_results.grid_columnconfigure(3, minsize=30)

        self.frame_results.grid_rowconfigure(0, minsize=100)
        self.frame_results.grid_rowconfigure(1, minsize=380)
        self.frame_results.grid_rowconfigure(2, minsize=130)
        self.frame_results.grid_rowconfigure(3, minsize=100)

        Label(self.frame_results,
              text='Результаты тестирований',
              font='sans-serif 18',
              bg='#2c386a',
              fg='white').grid(row=0, column=1)

        columns = ('1', '2', '3', '4', '5', '6', '7')

        # Создание таблицы результатов тестирований
        table = Treeview(self.frame_results,
                         show='headings',
                         columns=columns,
                         height=17)

        table.heading('1', text='Дата тестирования')
        table.column('1', width=125)

        table.heading('2', text='Имя')
        table.column('2', width=120)

        table.heading('3', text='Фамилия')
        table.column('3', width=120)

        table.heading('4', text='Группа')
        table.column('4', width=65)

        table.heading('5', text='Тема')
        table.column('5', width=160)

        table.heading('6', text='Процент правильных ответов')
        table.column('6', width=175)

        table.heading('7', text='Оценка')
        table.column('7', width=70)

        # Считывание результатов тестирований из файла в таблицу
        with open("./files/Результаты тестирований.csv", newline="") as f:
            for result in csv.reader(f):
                table.insert("", END, values=result)

        table.grid(row=1, column=1)

        Button(self.frame_results,
               text="Выход",
               cursor='hand2',
               font='sans-serif 16',
               command=lambda: self.back(),
               bd=1,
               bg='white').grid(row=2, column=1, sticky='w')

        self.results.protocol("WM_DELETE_WINDOW", self.exit)
        self.results.iconbitmap('icon.ico')
        self.results.mainloop()

    # Событие при нажатии кнопки "Назад"
    def back(self):
        self.results.quit()
        self.results.destroy()

    # Событие при закрытии окна
    def exit(self):
        raise SystemExit
