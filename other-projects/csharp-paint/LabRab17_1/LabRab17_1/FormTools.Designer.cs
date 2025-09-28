namespace LabRab17_1 {
    partial class FormTools {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing) {
            if (disposing && (components != null)) {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent() {
            this.label1 = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.rbEraser = new System.Windows.Forms.RadioButton();
            this.rbImage = new System.Windows.Forms.RadioButton();
            this.rbText = new System.Windows.Forms.RadioButton();
            this.rbRectangle = new System.Windows.Forms.RadioButton();
            this.rbEllipse = new System.Windows.Forms.RadioButton();
            this.rbLine = new System.Windows.Forms.RadioButton();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.label2 = new System.Windows.Forms.Label();
            this.numPenThickness = new System.Windows.Forms.NumericUpDown();
            this.butLineColor = new System.Windows.Forms.Button();
            this.butFillColor = new System.Windows.Forms.Button();
            this.colorDialog1 = new System.Windows.Forms.ColorDialog();
            this.label3 = new System.Windows.Forms.Label();
            this.cbFillStyle = new System.Windows.Forms.ComboBox();
            this.label4 = new System.Windows.Forms.Label();
            this.textBox1 = new System.Windows.Forms.TextBox();
            this.label5 = new System.Windows.Forms.Label();
            this.cbFonts = new System.Windows.Forms.ComboBox();
            this.label8 = new System.Windows.Forms.Label();
            this.textBox2 = new System.Windows.Forms.TextBox();
            this.button1 = new System.Windows.Forms.Button();
            this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
            this.butFillLines = new System.Windows.Forms.Button();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.numPenThickness)).BeginInit();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(9, 185);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(121, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Предварительный вид";
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.rbEraser);
            this.groupBox1.Controls.Add(this.rbImage);
            this.groupBox1.Controls.Add(this.rbText);
            this.groupBox1.Controls.Add(this.rbRectangle);
            this.groupBox1.Controls.Add(this.rbEllipse);
            this.groupBox1.Controls.Add(this.rbLine);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(127, 157);
            this.groupBox1.TabIndex = 1;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Выбор примитива";
            // 
            // rbEraser
            // 
            this.rbEraser.AutoSize = true;
            this.rbEraser.Location = new System.Drawing.Point(12, 134);
            this.rbEraser.Name = "rbEraser";
            this.rbEraser.Size = new System.Drawing.Size(62, 17);
            this.rbEraser.TabIndex = 21;
            this.rbEraser.TabStop = true;
            this.rbEraser.Text = "Ластик";
            this.rbEraser.UseVisualStyleBackColor = true;
            this.rbEraser.CheckedChanged += new System.EventHandler(this.radioButtons_CheckedChanged);
            // 
            // rbImage
            // 
            this.rbImage.AutoSize = true;
            this.rbImage.Location = new System.Drawing.Point(12, 111);
            this.rbImage.Name = "rbImage";
            this.rbImage.Size = new System.Drawing.Size(95, 17);
            this.rbImage.TabIndex = 4;
            this.rbImage.TabStop = true;
            this.rbImage.Text = "Изображение";
            this.rbImage.UseVisualStyleBackColor = true;
            this.rbImage.CheckedChanged += new System.EventHandler(this.radioButtons_CheckedChanged);
            // 
            // rbText
            // 
            this.rbText.AutoSize = true;
            this.rbText.Location = new System.Drawing.Point(12, 88);
            this.rbText.Name = "rbText";
            this.rbText.Size = new System.Drawing.Size(55, 17);
            this.rbText.TabIndex = 3;
            this.rbText.TabStop = true;
            this.rbText.Text = "Текст";
            this.rbText.UseVisualStyleBackColor = true;
            this.rbText.CheckedChanged += new System.EventHandler(this.radioButtons_CheckedChanged);
            // 
            // rbRectangle
            // 
            this.rbRectangle.AutoSize = true;
            this.rbRectangle.Location = new System.Drawing.Point(12, 65);
            this.rbRectangle.Name = "rbRectangle";
            this.rbRectangle.Size = new System.Drawing.Size(105, 17);
            this.rbRectangle.TabIndex = 2;
            this.rbRectangle.TabStop = true;
            this.rbRectangle.Text = "Прямоугольник";
            this.rbRectangle.UseVisualStyleBackColor = true;
            this.rbRectangle.CheckedChanged += new System.EventHandler(this.radioButtons_CheckedChanged);
            // 
            // rbEllipse
            // 
            this.rbEllipse.AutoSize = true;
            this.rbEllipse.Location = new System.Drawing.Point(12, 42);
            this.rbEllipse.Name = "rbEllipse";
            this.rbEllipse.Size = new System.Drawing.Size(62, 17);
            this.rbEllipse.TabIndex = 1;
            this.rbEllipse.TabStop = true;
            this.rbEllipse.Text = "Эллипс";
            this.rbEllipse.UseVisualStyleBackColor = true;
            this.rbEllipse.CheckedChanged += new System.EventHandler(this.radioButtons_CheckedChanged);
            // 
            // rbLine
            // 
            this.rbLine.AutoSize = true;
            this.rbLine.Location = new System.Drawing.Point(12, 19);
            this.rbLine.Name = "rbLine";
            this.rbLine.Size = new System.Drawing.Size(57, 17);
            this.rbLine.TabIndex = 0;
            this.rbLine.TabStop = true;
            this.rbLine.Text = "Линия";
            this.rbLine.UseVisualStyleBackColor = true;
            this.rbLine.CheckedChanged += new System.EventHandler(this.radioButtons_CheckedChanged);
            // 
            // pictureBox1
            // 
            this.pictureBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.pictureBox1.BackColor = System.Drawing.Color.White;
            this.pictureBox1.Location = new System.Drawing.Point(12, 201);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(215, 243);
            this.pictureBox1.TabIndex = 2;
            this.pictureBox1.TabStop = false;
            // 
            // label2
            // 
            this.label2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(245, 12);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(171, 13);
            this.label2.TabIndex = 3;
            this.label2.Text = "Толщина линии/размер шрифта";
            // 
            // numPenThickness
            // 
            this.numPenThickness.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.numPenThickness.Location = new System.Drawing.Point(248, 34);
            this.numPenThickness.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.numPenThickness.Name = "numPenThickness";
            this.numPenThickness.Size = new System.Drawing.Size(83, 20);
            this.numPenThickness.TabIndex = 4;
            this.numPenThickness.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.numPenThickness.ValueChanged += new System.EventHandler(this.numPenThickness_ValueChanged);
            // 
            // butLineColor
            // 
            this.butLineColor.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.butLineColor.Location = new System.Drawing.Point(248, 77);
            this.butLineColor.Name = "butLineColor";
            this.butLineColor.Size = new System.Drawing.Size(185, 23);
            this.butLineColor.TabIndex = 5;
            this.butLineColor.Text = "Цвет линии примитива";
            this.butLineColor.UseVisualStyleBackColor = true;
            this.butLineColor.Click += new System.EventHandler(this.butLineColor_Click);
            // 
            // butFillColor
            // 
            this.butFillColor.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.butFillColor.Location = new System.Drawing.Point(248, 106);
            this.butFillColor.Name = "butFillColor";
            this.butFillColor.Size = new System.Drawing.Size(185, 23);
            this.butFillColor.TabIndex = 6;
            this.butFillColor.Text = "Цвет заливки примитива/текста";
            this.butFillColor.UseVisualStyleBackColor = true;
            this.butFillColor.Click += new System.EventHandler(this.butFillColor_Click);
            // 
            // label3
            // 
            this.label3.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(245, 175);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(140, 13);
            this.label3.TabIndex = 7;
            this.label3.Text = "Стиль заливки примитива";
            // 
            // cbFillStyle
            // 
            this.cbFillStyle.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbFillStyle.FormattingEnabled = true;
            this.cbFillStyle.Location = new System.Drawing.Point(248, 191);
            this.cbFillStyle.Name = "cbFillStyle";
            this.cbFillStyle.Size = new System.Drawing.Size(185, 21);
            this.cbFillStyle.TabIndex = 8;
            this.cbFillStyle.SelectedIndexChanged += new System.EventHandler(this.Changed);
            // 
            // label4
            // 
            this.label4.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(245, 224);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(37, 13);
            this.label4.TabIndex = 9;
            this.label4.Text = "Текст";
            // 
            // textBox1
            // 
            this.textBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.textBox1.Location = new System.Drawing.Point(248, 240);
            this.textBox1.Multiline = true;
            this.textBox1.Name = "textBox1";
            this.textBox1.Size = new System.Drawing.Size(185, 57);
            this.textBox1.TabIndex = 10;
            this.textBox1.TextChanged += new System.EventHandler(this.Changed);
            // 
            // label5
            // 
            this.label5.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(245, 316);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(41, 13);
            this.label5.TabIndex = 11;
            this.label5.Text = "Шрифт";
            // 
            // cbFonts
            // 
            this.cbFonts.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbFonts.FormattingEnabled = true;
            this.cbFonts.Location = new System.Drawing.Point(248, 332);
            this.cbFonts.Name = "cbFonts";
            this.cbFonts.Size = new System.Drawing.Size(185, 21);
            this.cbFonts.TabIndex = 12;
            this.cbFonts.SelectedIndexChanged += new System.EventHandler(this.Changed);
            // 
            // label8
            // 
            this.label8.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(245, 368);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(77, 13);
            this.label8.TabIndex = 17;
            this.label8.Text = "Изображение";
            // 
            // textBox2
            // 
            this.textBox2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.textBox2.Location = new System.Drawing.Point(247, 384);
            this.textBox2.Name = "textBox2";
            this.textBox2.Size = new System.Drawing.Size(186, 20);
            this.textBox2.TabIndex = 18;
            // 
            // button1
            // 
            this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button1.Location = new System.Drawing.Point(247, 410);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(63, 23);
            this.button1.TabIndex = 19;
            this.button1.Text = "Обзор...";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // openFileDialog1
            // 
            this.openFileDialog1.FileName = "openFileDialog1";
            // 
            // butFillLines
            // 
            this.butFillLines.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.butFillLines.Location = new System.Drawing.Point(248, 135);
            this.butFillLines.Name = "butFillLines";
            this.butFillLines.Size = new System.Drawing.Size(185, 23);
            this.butFillLines.TabIndex = 20;
            this.butFillLines.Text = "Цвет линий примитива в стиле заливки";
            this.butFillLines.UseVisualStyleBackColor = true;
            this.butFillLines.Click += new System.EventHandler(this.butFillLines_Click);
            // 
            // FormTools
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(445, 456);
            this.Controls.Add(this.butFillLines);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.textBox2);
            this.Controls.Add(this.label8);
            this.Controls.Add(this.cbFonts);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.textBox1);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.cbFillStyle);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.butFillColor);
            this.Controls.Add(this.butLineColor);
            this.Controls.Add(this.numPenThickness);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.pictureBox1);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.label1);
            this.Name = "FormTools";
            this.Text = "FormTools";
            this.TopMost = true;
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FormTools_FormClosing);
            this.LocationChanged += new System.EventHandler(this.FormTools_LocationChanged);
            this.Resize += new System.EventHandler(this.FormTools_Resize);
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.numPenThickness)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.RadioButton rbImage;
        private System.Windows.Forms.RadioButton rbText;
        private System.Windows.Forms.RadioButton rbRectangle;
        private System.Windows.Forms.RadioButton rbEllipse;
        private System.Windows.Forms.RadioButton rbLine;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.NumericUpDown numPenThickness;
        private System.Windows.Forms.Button butLineColor;
        private System.Windows.Forms.Button butFillColor;
        private System.Windows.Forms.ColorDialog colorDialog1;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.ComboBox cbFillStyle;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.TextBox textBox1;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.ComboBox cbFonts;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.OpenFileDialog openFileDialog1;
        public System.Windows.Forms.TextBox textBox2;
        private System.Windows.Forms.Button butFillLines;
        private System.Windows.Forms.RadioButton rbEraser;
    }
}