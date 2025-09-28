using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;
using System.Security.Cryptography.X509Certificates;

namespace DrawingLibrary {
    public class Drawing {
        public static void DrawLine(Pen p, int x1, int y1, int x2, int y2, Graphics g) {
            g.DrawLine(p, x1, y1, x2, y2);
        }

        public static void DrawEllipse(Pen p, Brush b, int x1, int y1, int x2, int y2, Graphics g) {
            g.DrawEllipse(p, x1, y1, x2 - x1, y2 - y1);
            g.FillEllipse(b, x1, y1, x2 - x1, y2 - y1);
        }

        public static void DrawRectangle(Pen p, Brush b, int x1, int y1, int x2, int y2, Graphics g) {
            g.DrawRectangle(p, Math.Min(x1, x2), Math.Min(y1, y2), Math.Abs(x2 - x1), Math.Abs(y2 - y1));
            g.FillRectangle(b, Math.Min(x1, x2), Math.Min(y1, y2), Math.Abs(x2 - x1), Math.Abs(y2 - y1));
        }

        public static void DrawString(string text, string fontName, float fontSize,  Brush b, int x1, int y1, int x2, int y2, Graphics g) {
            g.DrawString(text, new Font(fontName, fontSize), b, new RectangleF(Math.Min(x1, x2), Math.Min(y1, y2), Math.Abs(x2 - x1), Math.Abs(y2 - y1)));
        }

        public static void DrawImage(string image, int x1, int y1, int x2, int y2, Graphics g) {
            g.DrawImage(Image.FromFile(image), new Rectangle(Math.Min(x1, x2), Math.Min(y1, y2), Math.Abs(x2 - x1), Math.Abs(y2 - y1)));
        }
    }
}
