using System.Windows;

namespace EmploymentService
{
	/// <summary>
	/// Логика взаимодействия для WindowChangePassword.xaml
	/// </summary>
	public partial class WindowChangePassword : Window
	{
		public WindowChangePassword()
		{
			InitializeComponent();
		}

		private void ButtonChangePassword_Click(object sender, RoutedEventArgs e)
		{
			if (!string.IsNullOrWhiteSpace(PasswordBoxNewPassword.Password))
			{
				WorkDatabase.ChangePassword(PasswordBoxNewPassword.Password);

				MessageBox.Show("Пароль успешно изменен", "Грац!");

				this.Close();
			}
			else
			{
				MessageBox.Show("Введите корректный пароль", "Ошибка");
			}
		}
	}
}
