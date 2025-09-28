using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Drawing = DrawingLibrary.Drawing;

namespace LabRab17_1 {
    public partial class FormTools : Form {
        public Pen p = new Pen(Color.Green, 1);
        public SolidBrush b = new SolidBrush(Color.Orange);
        public Brush hb;
        public Color fl = Color.Black;
        public Color bg = Color.White;

        public FormTools() {
            InitializeComponent();

            System.Drawing.Text.InstalledFontCollection fonts = new System.Drawing.Text.InstalledFontCollection();

            foreach (FontFamily font in fonts.Families)
                cbFonts.Items.Add(font.Name);

            cbFillStyle.Items.Add("No HatchStyle");

            foreach (string styleName in Enum.GetNames(typeof(HatchStyle))) 
                cbFillStyle.Items.Add(styleName);

            cbFillStyle.SelectedIndex = 0;
            cbFonts.Text = DefaultFont.Name;
        }

        public void radioButtons_CheckedChanged(object sender, EventArgs e) {
            RadioButton rb = (RadioButton)sender;

            if (!rb.Checked)
                return;

            PBView();
        }

        public void PBView() {
            if (!rbEraser.Checked) {
                pictureBox1.Refresh();
            }

            Graphics g = pictureBox1.CreateGraphics();

            switch (FindRB()) {
                case 1:
                    Drawing.DrawLine(p, 30, 30, 100, 100, g);
                    break;

                case 2:
                    if (cbFillStyle.SelectedIndex == 0)
                        Drawing.DrawEllipse(p, b, 30, 30, 100, 100, g);
                    else {
                        hb = new HatchBrush((HatchStyle)Enum.Parse(typeof(HatchStyle), cbFillStyle.SelectedItem.ToString(), true), fl, b.Color);

                        Drawing.DrawEllipse(p, hb, 30, 30, 100, 100, g);
                    }
                    break;

                case 3:
                    if (cbFillStyle.SelectedIndex == 0)
                        Drawing.DrawRectangle(p, b, 30, 30, 100, 100, g);
                    else {
                        hb = new HatchBrush((HatchStyle)Enum.Parse(typeof(HatchStyle), cbFillStyle.SelectedItem.ToString(), true), fl, b.Color);

                        Drawing.DrawRectangle(p, hb, 30, 30, 100, 100, g);
                    }
                    break;

                case 4:
                    Drawing.DrawString(textBox1.Text, cbFonts.SelectedItem.ToString(), p.Width, b, 30, 30, 100, 100, g);
                    break;

                case 5:
                    try {
                        Drawing.DrawImage(textBox2.Text, 30, 30, 200, 200, g);
                    }
                    catch {
                        MessageBox.Show("Выберите корректный файл изображения", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }
                    break;

                case 6:
                    Color prevP = p.Color;
                    Color prevB = b.Color;

                    p.Color = bg;
                    b.Color = bg;
                    
                    Drawing.DrawRectangle(p, b, 30, 30, 70, 70, g);

                    p.Color = prevP;
                    b.Color = prevB;

                    break;
            }
        }

        public void numPenThickness_ValueChanged(object sender, EventArgs e) {
            p.Width = Convert.ToInt32(numPenThickness.Value);
            PBView();
        }

        public int FindRB() {
            if (rbLine.Checked)
                return 1;

            if (rbEllipse.Checked)
                return 2;

            if (rbRectangle.Checked)
                return 3;

            if (rbText.Checked)
                return 4;

            if (rbImage.Checked)
                return 5;

            if (rbEraser.Checked)
                return 6;
                
            return 0;
        }

        private void butLineColor_Click(object sender, EventArgs e) {
            colorDialog1.ShowDialog();
            p.Color = colorDialog1.Color;
            PBView();
        }

        private void butFillColor_Click(object sender, EventArgs e) {
            colorDialog1.ShowDialog();
            b.Color = colorDialog1.Color;
            PBView();
        }

        private void butFillLines_Click(object sender, EventArgs e) {
            colorDialog1.ShowDialog();
            fl = colorDialog1.Color;
            PBView();
        }

        private void button1_Click(object sender, EventArgs e) {
            openFileDialog1.Filter = "Image Files(*.bmp)|*.bmp|Image Files(*.jpg)|*.jpg|Image Files(*.jpeg)|*.jpeg|Image Files(*.tiff)|*.tiff|Image Files(*.tif)|*.tif|Image Files(*.png)|*.png|All files (*.*)|*.*";

            openFileDialog1.ShowDialog();
            textBox2.Text = openFileDialog1.FileName;
            PBView();
        }

        private void FormTools_FormClosing(object sender, FormClosingEventArgs e) {
            e.Cancel = true;
            this.Hide();
        }

        private void Changed(object sender, EventArgs e) {
            PBView();
        }

        private void FormTools_Resize(object sender, EventArgs e) {
            if (this.WindowState == FormWindowState.Normal)
                PBView();
        }

        private void FormTools_LocationChanged(object sender, EventArgs e) {
            PBView();
        }
    }
}
