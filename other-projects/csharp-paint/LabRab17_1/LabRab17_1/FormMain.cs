using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Drawing = DrawingLibrary.Drawing;

namespace LabRab17_1 {
    public partial class FormMain : Form {
        FormTools formTools = new FormTools();
        public Point A;
        bool flag = true;
        Bitmap pic;
        Graphics g;
        const int countLogs = 5;

        class Log {
            public string logName;
            public Bitmap logPic;
            public DateTime logDateTime;

            public Log(string name, Bitmap pic, DateTime dateTime) {
                logName = name;
                logPic = pic;
                logDateTime = dateTime;
            }
        }

        Log[] logs = new Log[countLogs];

        public FormMain() {
            InitializeComponent();
            formTools.Show();
            pic = new Bitmap(pictureBox1.Width, pictureBox1.Height);
            g = Graphics.FromImage(pic);
            g.Clear(formTools.bg);
            
            for (byte i = 0; i < logs.Length; i++) {
                logs[i] = new Log("", pic, default);
            }
        }

        private void pictureBox1_MouseDown(object sender, MouseEventArgs e) {
            A = new Point(e.X, e.Y);
        }

        private void pictureBox1_MouseUp(object sender, MouseEventArgs e) {
            if (flag)
                Changes();

            switch (formTools.FindRB()) {
                case 1:
                    Logging(
                        $"Создание линии, начальная точка: ({A.X};{A.Y}), конечная точка: ({e.X};{e.Y})."
                    );

                    Drawing.DrawLine(formTools.p, A.X, A.Y, e.X, e.Y, g);
                    break;

                case 2:
                    Logging(
                        $"Создание эллипса, начальная точка: ({A.X};{A.Y}), конечная точка: ({e.X};{e.Y})."
                    );

                    if ((formTools.Controls["cbFillStyle"] as ComboBox).SelectedIndex == 0)
                        Drawing.DrawEllipse(formTools.p, formTools.b, A.X, A.Y, e.X, e.Y, g);
                    else {
                        formTools.hb = new HatchBrush((HatchStyle)Enum.Parse(typeof(HatchStyle), (formTools.Controls["cbFillStyle"] as ComboBox).SelectedItem.ToString(), true), formTools.fl, formTools.b.Color);

                        Drawing.DrawEllipse(formTools.p, formTools.hb, A.X, A.Y, e.X, e.Y, g);
                    }
                    break;

                case 3:
                    Logging(
                        $"Создание прямоугольника, начальная точка: ({A.X};{A.Y}), конечная точка: ({e.X};{e.Y})."
                    );

                    if ((formTools.Controls["cbFillStyle"] as ComboBox).SelectedIndex == 0)
                        Drawing.DrawRectangle(formTools.p, formTools.b, A.X, A.Y, e.X, e.Y, g);
                    else {
                        formTools.hb = new HatchBrush((HatchStyle)Enum.Parse(typeof(HatchStyle), (formTools.Controls["cbFillStyle"] as ComboBox).SelectedItem.ToString(), true), formTools.fl, formTools.b.Color);

                        Drawing.DrawRectangle(formTools.p, formTools.hb, A.X, A.Y, e.X, e.Y, g);
                    }
                    break;

                case 4:
                    Logging(
                        $"Вставка текста, начальная точка: ({A.X};{A.Y}), конечная точка: ({e.X};{e.Y})."
                    );

                    Drawing.DrawString(formTools.Controls["textBox1"].Text, (formTools.Controls["cbFonts"] as ComboBox).SelectedItem.ToString(), formTools.p.Width, formTools.b, A.X, A.Y, e.X, e.Y, g);
                    break;

                case 5:
                    try {
                        Logging(
                            $"Вставка изображения, начальная точка: ({A.X};{A.Y}), конечная точка: ({e.X};{e.Y})."
                        );

                        Drawing.DrawImage(formTools.Controls["textBox2"].Text, A.X, A.Y, e.X, e.Y, g);
                    }
                    catch {
                        MessageBox.Show("Выберите корректный файл изображения", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }
                    break;

                case 6:
                    Logging(
                        $"Очистка области ластиком, начальная точка: ({A.X};{A.Y}), конечная точка: ({e.X};{e.Y})."
                    );

                    Color prevP = formTools.p.Color;
                    Color prevB = formTools.b.Color;

                    formTools.p.Color = formTools.bg;
                    formTools.b.Color = formTools.bg;

                    Drawing.DrawRectangle(formTools.p, formTools.b, A.X, A.Y, e.X, e.Y, g);

                    formTools.p.Color = prevP;
                    formTools.b.Color = prevB;
                    break;
            }
            
            pictureBox1.Image = pic;
        }

        private void newToolStripMenuItem_Click(object sender, EventArgs e) {
            if (flag == false)
                if (SaveFile() == 0) {
                    Logging(
                        "Создание нового рисунка."
                    );

                    Changes();
                    g.Clear(formTools.bg);
                    pictureBox1.Refresh();
                }
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e) {
            formTools.TopMost = false;
            openFileDialog1.ShowDialog();
            try {
                Logging(
                    $"Открытие файла {openFileDialog1.FileName}."
                );

                pic = (Bitmap)Image.FromFile(openFileDialog1.FileName);
                g.Clear(formTools.bg);
                pictureBox1.Image = pic;
                g = Graphics.FromImage(pic);
                Changes();
            }
            catch { }

            formTools.TopMost = true;
        }

        private void saveToolStripMenuItem_Click(object sender, EventArgs e) {
            if (flag == false)
                if (SaveFile() == 0)
                    Changes();
        }

        private void clearToolStripMenuItem_Click(object sender, EventArgs e) {
            Logging(
                "Очистка рисунка."
            );

            g.Clear(formTools.bg);
            pictureBox1.Refresh();
            if (flag == false)
                Changes();
        }

        private void logsToolStripMenuItem_Click(object sender, EventArgs e) {
            using (Form logForm = new Form()) {
                logForm.Name = "logs";
                logForm.Text = "logs";
                logForm.Size = new Size(400, 420);

                int posY = 20, j = 1;

                for (int i = 0; i < logs.Length; i++)
                    if (logs[i].logName != "") {
                        Label l = new Label();
                        l.Name = "log" + j;
                        l.Text = logs[i].logDateTime.ToString();
                        l.Location = new Point(12, posY);
                        l.AutoSize = true;
                        logForm.Controls.Add(l);

                        TextBox tb = new TextBox();
                        tb.Name = "tbLog" + j;
                        tb.Text = logs[i].logName;
                        tb.Location = new Point(30, posY+18);
                        tb.Size = new Size(317, 34);
                        tb.Multiline = true;
                        tb.TabStop = false;
                        logForm.Controls.Add(tb);

                        posY += 70;
                        j++;
                    }

                logForm.ShowDialog();
            }
        }

        private void showFormToolsToolStripMenuItem_Click(object sender, EventArgs e) {
            formTools.Show();
            formTools.PBView();
        }

        private int Changes() {
            if (this.Text[this.Text.Length - 1] != '*') {
                this.Text += '*';
                flag = false;
                return 1;
            }

            if (this.Text[this.Text.Length - 1] == '*') {
                this.Text = this.Text.Remove(this.Text.Length - 1, 1);
                flag = true;
                return 2;
            }

            return 0;
        }

        private int SaveFile() {
            if (flag == false) {
                formTools.TopMost = false;

                saveFileDialog1.Filter = "Image Files(*.bmp)|*.bmp|Image Files(*.jpg)|*.jpg|Image Files(*.jpeg)|*.jpeg|Image Files(*.tiff)|*.tiff|Image Files(*.tif)|*.tif|Image Files(*.png)|*.png|All files (*.*)|*.*";

                if (saveFileDialog1.ShowDialog() != DialogResult.OK) 
                    return 1;

                try {
                    pic.Save(saveFileDialog1.FileName);
                }
                catch (Exception e) {
                    if (e is System.Runtime.InteropServices.ExternalException)
                        MessageBox.Show("Сохраните файл под новым именем.", "Ошибка", MessageBoxButtons.OK,
                            MessageBoxIcon.Error);
                    return 2;
                }

                formTools.TopMost = true;
            }

            return 0;
        }

        private void FormMain_FormClosing(object sender, FormClosingEventArgs e) {
            if (flag == false) {
                int s = SaveFile();

                if (s == 1) {
                    if (MessageBox.Show("Вы не сохранили файл. Вы точно хотите выйти?", "Выход", MessageBoxButtons.YesNo,
                            MessageBoxIcon.Question) != DialogResult.Yes)
                        e.Cancel = true;
                }
                else if (s == 2)
                    e.Cancel = true;
            }
        }

        private void Logging(string log) {
            if (logs[logs.Length-1].logName != "")
                for (byte i = 0; i < logs.Length-1; i++)
                    logs[i] = logs[i + 1];

            logs[logs.Length-1] = new Log(log, pic, DateTime.Now);
        }

        private void FormMain_KeyDown(object sender, KeyEventArgs e) {
            if (e.Control && e.KeyCode == Keys.Z) {
                if (logs[logs.Length - 1].logName != "") {
                    for (byte i = (byte)(logs.Length - 1); i > 0; i--)
                        logs[i] = logs[i - 1];

                    if (logs[0].logName != "")
                        logs[0] = new Log("", null, default);

                    pic = (Bitmap)logs[logs.Length-1].logPic.Clone();
                    g.Clear(formTools.bg);
                    pictureBox1.Refresh();
                    g = Graphics.FromImage(pic);
                    pictureBox1.Image = pic;
                }
                else {
                    MessageBox.Show("Больше отменять нельзя.", "Информация", MessageBoxButtons.OK,
                        MessageBoxIcon.Information);
                }
            }
        }
    }
}
