using System.Windows;
using System.Windows.Controls;

namespace EmploymentService
{
	/// <summary>
	/// Логика взаимодействия для WindowAuthorization.xaml
	/// </summary>
	public partial class WindowAuthorization : Window
	{
		public WindowAuthorization()
		{
			InitializeComponent();
		}

		private void ButtonChangeEntryMethod_Click(object sender, RoutedEventArgs e)
		{
			if (((Button)sender).Content.ToString() == "Регистрация")
			{
				((Button)sender).Content = "Авторизация";

				GroupBoxAccountType.Visibility = Visibility.Visible;

				ButtonEntryMethodGo.Margin = new Thickness(135, 225, 0, 0);
				ButtonEntryMethodGo.Content = "Зарегистрироваться";

				ButtonAuthorizationCancel.Visibility = Visibility.Hidden;
			}
			else if (((Button)sender).Content.ToString() == "Авторизация")
			{
				((Button)sender).Content = "Регистрация";

				GroupBoxAccountType.Visibility = Visibility.Hidden;

				ButtonEntryMethodGo.Margin = new Thickness(135, 155, 0, 0);
				ButtonEntryMethodGo.Content = "Авторизоваться";

				ButtonAuthorizationCancel.Visibility = Visibility.Visible;
			}
		}

		private void ButtonEntryMethodGo_Click(object sender, RoutedEventArgs e)
		{
			if (((Button)sender).Content.ToString() == "Авторизоваться")
			{
				if (WorkDatabase.Authorization(TextBoxLogin.Text, PasswordBoxPassword.Password))
				{
					OpenMainMenu();
				}
				else
				{
					MessageBox.Show("Неправильный логин или пароль", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
				}
			}
			else if (((Button)sender).Content.ToString() == "Зарегистрироваться")
			{
				string accountType;

				if (RadioButtonEmployee.IsChecked.Value)
				{
					accountType = "Employee";
				}
				else
				{
					accountType = "Organization";
				}

				if (WorkDatabase.Registration(TextBoxLogin.Text, PasswordBoxPassword.Password, accountType))
				{
					OpenMainMenu();
				}
				else
				{
					MessageBox.Show("Пользователь с данным логином уже зарегистрирован в системе", "Ошибка", MessageBoxButton.OK, MessageBoxImage.Error);
				}
			}
		}

		private void ButtonAuthorizationCancel_Click(object sender, RoutedEventArgs e)
		{
			WorkDatabase.CurrentUser.Login = null;
			WorkDatabase.CurrentUser.AccountType = null;
			WorkDatabase.CurrentUser.IdMatch = 0;

			OpenMainMenu();
		}

		void OpenMainMenu()
		{
			WindowMainMenu windowMainMenu = new WindowMainMenu();

			windowMainMenu.Show();

			this.Close();
		}
	}
}
