import os
import os.path
import re
import datetime
from random import randint, seed
from tkinter import Label, Button, Toplevel, Frame, LEFT, Radiobutton, IntVar, messagebox
import input_information


# Класс окна теста
class Test:
    def __init__(self, master, theme_file):
        self.test = Toplevel(master)
        self.test.configure(background='#2c386a')
        self.test.title('Обучающее приложение: Астрономия')
        self.test.geometry('1000x800')
        self.test.resizable(False, False)

        self.test_questions = []
        self.first_question = True
        self.question_num = 1
        self.current_correct_answer = 0
        self.count_correct_answers = 0
        self.count_questions = 10
        self.count_themes = 1
        self.list = []
        self.list.append(theme_file)
        # Запись всех файлов в список, если выбрано Итоговое тестирование в главном меню
        if theme_file == '':
            self.count_questions = 20
            self.themes = os.listdir('./files/themes')
            self.list = ['{}{}'.format("./files/themes/", i) for i in self.themes]
            self.count_themes = len(self.list)
            if self.count_themes < 2:
                messagebox.showerror('Ошибка', 'Тем должно быть минимум 2')
                self.test.destroy()
                return

        self.frame_question = Frame(self.test, bg='#2c386a')
        self.frame_question.grid(row=0, column=0, columnspan=5, rowspan=7)

        self.frame_question.grid_columnconfigure(0, minsize=30)
        self.frame_question.grid_columnconfigure(1, minsize=100)
        self.frame_question.grid_columnconfigure(3, minsize=100)
        self.frame_question.grid_columnconfigure(4, minsize=30)

        self.frame_question.grid_rowconfigure(0, minsize=100)
        self.frame_question.grid_rowconfigure(1, minsize=140)
        self.frame_question.grid_rowconfigure(2, minsize=80)
        self.frame_question.grid_rowconfigure(3, minsize=80)
        self.frame_question.grid_rowconfigure(4, minsize=80)
        self.frame_question.grid_rowconfigure(5, minsize=220)
        self.frame_question.grid_rowconfigure(6, minsize=100)

        self.lab1 = Label(self.frame_question,
                          width=52,
                          font='sans-serif 18',
                          bg='#2c386a',
                          fg='white')

        self.lab1.grid(row=0, column=2)

        self.label_question = Label(self.frame_question,
                                    wraplength=540,
                                    justify=LEFT,
                                    font='sans-serif 16',
                                    bg='#2c386a',
                                    fg='white')

        self.label_question.grid(row=1, column=2)

        # Радиобаттоны по умолчанию выключены
        self.answers = IntVar()
        self.answers.set(0)

        self.rbut_answer1 = Radiobutton(self.frame_question,
                                        wraplength=560,
                                        variable=self.answers,
                                        value=1,
                                        width=54,
                                        anchor='w',
                                        justify=LEFT,
                                        font='sans-serif 14',
                                        bg='#2c386a',
                                        selectcolor='black',
                                        activebackground='#2c386a',
                                        activeforeground='white',
                                        fg='white')

        self.rbut_answer1.grid(row=2, column=2)

        self.rbut_answer2 = Radiobutton(self.frame_question,
                                        wraplength=560,
                                        variable=self.answers,
                                        value=2,
                                        width=54,
                                        anchor='w',
                                        justify=LEFT,
                                        font='sans-serif 14',
                                        bg='#2c386a',
                                        selectcolor='black',
                                        activebackground='#2c386a',
                                        activeforeground='white',
                                        fg='white')

        self.rbut_answer2.grid(row=3, column=2)

        self.rbut_answer3 = Radiobutton(self.frame_question,
                                        wraplength=560,
                                        variable=self.answers,
                                        value=3,
                                        width=54,
                                        anchor='w',
                                        justify=LEFT,
                                        font='sans-serif 14',
                                        bg='#2c386a',
                                        selectcolor='black',
                                        activebackground='#2c386a',
                                        activeforeground='white',
                                        fg='white')

        self.rbut_answer3.grid(row=4, column=2)

        but1 = Button(self.frame_question,
                      text="Выход",
                      cursor='hand2',
                      font='sans-serif 16',
                      bd=1,
                      bg='white')

        but1.bind('<Button-1>', self.but_exit)
        but1.grid(row=6, column=1)

        self.but2 = Button(self.frame_question,
                           text="Следующий вопрос",
                           cursor='hand2',
                           font='sans-serif 16',
                           command=lambda: self.next_question(),
                           bd=1,
                           bg='white')

        self.but2.grid(row=6, column=2)

        self.test.protocol("WM_DELETE_WINDOW", self.exit)
        self.test.iconbitmap('icon.ico')
        # Сформировать первый вопрос
        self.next_question()
        self.test.mainloop()

    # Сформировать вопрос
    def next_question(self):
        # Проверка правильности ответа
        if not self.first_question:
            if self.answers.get().__str__() == self.current_correct_answer.__str__():
                self.count_correct_answers += 1
        else:
            self.first_question = False

        # Изменение текста кнопки "Следующий вопрос" при последнем вопросе
        if self.but2.config('text')[4] == 'Закончить тест':
            # Формирование результатов
            test_end_time = datetime.datetime.now().strftime("%d.%m.%Y %H:%M:%S")
            if len(self.list) > 1:
                test_theme = 'Итоговое тестирование'
            else:
                test_theme = self.theme_name(self.list[0])
            test_percentage_of_correct_answers = self.count_correct_answers / self.count_questions * 100
            test_grade = 2
            if test_percentage_of_correct_answers >= 90:
                test_grade = 5
            elif test_percentage_of_correct_answers >= 70:
                test_grade = 4
            elif test_percentage_of_correct_answers >= 60:
                test_grade = 3

            # Формирование списка содержимого для результата
            result = [test_end_time,
                      '',
                      '',
                      '',
                      test_theme,
                      f"{test_percentage_of_correct_answers:.{0}f}%",
                      test_grade.__str__()]

            self.test.withdraw()
            # Открытие и отправка неполного файла для заполнения вводимыми данными пользователя
            input_information.InputInformation(self.test, result)
            return

        # Рандомизация файла и вопроса из него для генерации блока вопроса
        while True:
            seed(version=2)
            theme_num = randint(1, self.count_themes)
            question_num = randint(1, 10)
            # Проверка нахождения на последнем вопросе
            if len(self.test_questions) == self.count_questions:
                break
            # Проверка нахождения срандомизированного файла и вопроса из него в списке предыдущих вопросов
            if [self.list[theme_num - 1], question_num] not in self.test_questions:
                self.test_questions.append([self.list[theme_num - 1], question_num])
                # Открытие и запись содержимого файла в переменную для работы с ним
                f = open(self.list[theme_num - 1], 'r', -1, 'utf-8')
                self.theme_content = f.read()
                f.close()
                break

        # Поиск индексов всех вхождений "Правильный ответ: " для проверки правильности выбора
        self.current_correct_answer = [m.end(0) for m in re.finditer('Правильный ответ: ', self.theme_content)]
        self.current_correct_answer = self.theme_content[
                                      self.current_correct_answer[
                                          self.test_questions[-1][1] - 1]:self.theme_content.find('\n',
                                                                                                  self.current_correct_answer[
                                                                                                      self.test_questions[
                                                                                                          -1][1] - 1])]

        # Отображение блока вопроса
        self.show_question(self.test_questions[-1])

        # Изменение текста кнопки "Следующий вопрос" при последнем вопросе
        if len(self.test_questions) == self.count_questions:
            self.but2.configure(text='Закончить тест')

    # Получение названия темы из текущего файла теории
    def theme_name(self, theory_file):
        f = open(theory_file, 'r', -1, 'utf-8')
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

    # Отображение блока вопроса
    def show_question(self, question):
        index = self.theme_content.find(f'Вопрос {question[1]}: ')
        self.question_text = ''
        if index != -1:
            index += len(f'Вопрос {question[1]}: ')
            # Поиск вариантов ответов искомого вопроса
            question_answers_index = [(m.start(0) - 1, m.end(0)) for m in
                                      re.finditer('Варианты ответов: ', self.theme_content)]
            question_answers = self.theme_content[
                               question_answers_index[question[1] - 1][1]:self.theme_content.find('\n',
                                                                                                  question_answers_index[
                                                                                                      question[1] - 1][
                                                                                                      1])].split(';')
            while index != question_answers_index[question[1] - 1][0]:
                # Запись текста вопроса в переменную для вывода на экран
                self.question_text += self.theme_content[index]
                index += 1
        # Отображение текста вопроса и вариантов ответов на окне
        self.lab1.configure(text=f'Вопрос №{self.question_num}')
        self.label_question.configure(text=self.question_text)
        self.rbut_answer1.configure(text=question_answers[0])
        self.rbut_answer2.configure(text=question_answers[1])
        self.rbut_answer3.configure(text=question_answers[2])
        self.answers.set(0)
        self.question_num += 1

    # Кнопка "Выход"
    def but_exit(self, event):
        self.test.quit()
        self.test.destroy()

    # Событие при закрытии окна
    def exit(self):
        raise SystemExit
