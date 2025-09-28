from tkinter import Label, Button, Tk, Toplevel
import themes
import test
import results


# Класс окна главного меню
class MainMenu:
    def __init__(self, master):
        self.main_menu = Toplevel(master)
        self.main_menu.configure(background='#2c386a')
        self.main_menu.title('Обучающее приложение: Астрономия')
        self.main_menu.geometry('500x500')
        self.main_menu.resizable(False, False)

        Label(self.main_menu,
              text='Главное меню',
              font='sans-serif 20',
              bg='#2c386a',
              fg='white').grid(row=1, column=1, sticky='wens')

        Button(self.main_menu,
               text='Обучение',
               cursor='hand2',
               command=self.open_themes,
               font='sans-serif 18',
               bg='white').grid(row=2, column=1, sticky='wens')

        Button(self.main_menu,
               text='Итоговое тестирование',
               cursor='hand2',
               command=self.open_final_test,
               font='sans-serif 18',
               bg='white').grid(row=3, column=1, sticky='wens', pady=20)

        Button(self.main_menu,
               text='Таблица результатов тестирований',
               cursor='hand2',
               command=self.open_results,
               font='sans-serif 18',
               wraplength=250,
               bg='white').grid(row=4, column=1, sticky='wens')

        # Редактирование размеров колонок и строк
        self.main_menu.grid_columnconfigure(0, minsize=105)
        self.main_menu.grid_columnconfigure(1, minsize=105)
        self.main_menu.grid_columnconfigure(2, minsize=105)

        self.main_menu.grid_rowconfigure(1, minsize=145)
        self.main_menu.grid_rowconfigure(5, minsize=145)

        # Событие при закрытии окна
        self.main_menu.protocol("WM_DELETE_WINDOW", self.exit)
        self.main_menu.iconbitmap('icon.ico')
        self.main_menu.mainloop()

    # Открытие окна списка тем
    def open_themes(self):
        self.main_menu.withdraw()
        themes.Themes(self.main_menu)
        self.main_menu.deiconify()

    # Открытие окна итогового тестирования
    def open_final_test(self):
        self.main_menu.withdraw()
        test.Test(self.main_menu, '')
        self.main_menu.deiconify()

    # Открытие окна результатов тестирований
    def open_results(self):
        self.main_menu.withdraw()
        results.Results(self.main_menu)
        self.main_menu.deiconify()

    # Событие при закрытии окна
    def exit(self):
        raise SystemExit


# Открытие окна главного меню, если этот файл был запущен отдельно
if __name__ == '__main__':
    root = Tk()
    root.withdraw()
    MainMenu(root)
