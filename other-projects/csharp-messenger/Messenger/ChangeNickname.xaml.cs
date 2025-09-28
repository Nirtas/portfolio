using System.Windows;
using System.Windows.Controls;

namespace Messenger
{
	/// <summary>
	/// Логика взаимодействия для ChangeNickname.xaml
	/// </summary>
	public partial class ChangeNickname : Window
	{
		public string newNickname { get; set; }

		public ChangeNickname()
		{
			InitializeComponent();
		}

		private void ButChangeNicknameOK_Click(object sender, RoutedEventArgs e)
		{
			newNickname = TbNewNickname.Text;
			this.Close();
		}

		private void TbNewNickname_TextChanged(object sender, TextChangedEventArgs e)
		{
			if (!string.IsNullOrWhiteSpace(TbNewNickname.Text))
			{
				ButChangeNicknameOK.IsEnabled = true;
			}
			else
			{
				ButChangeNicknameOK.IsEnabled = false;
			}
		}
	}
}
