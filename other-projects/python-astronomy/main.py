from tkinter import Label, Button, PhotoImage, Tk, CENTER
import main_menu


# Класс окна приветствия
class Main:
    def __init__(self, master):
        self.master = master
        self.master.geometry('700x500')
        self.master.title('Обучающее приложение: Астрономия')
        self.master.resizable(False, False)
        self.master.configure(background='#2c386a')
        self.photo = PhotoImage(file='./images/title-image.png')
        Label(self.master, image=self.photo, bd=0).pack()

        Button(self.master,
               text='Поехали!',
               cursor='hand2',
               command=self.open_main_menu,
               font='sans-serif 18',
               bg='white').pack(pady=20, anchor=CENTER)

        self.master.iconbitmap('icon.ico')
        self.master.mainloop()

    # Открытие главного меню
    def open_main_menu(self):
        self.master.withdraw()
        main_menu.MainMenu(self.master)
        self.master.deiconify()


# Создание окна экрана приветствия
root = Tk()
Main(root)
