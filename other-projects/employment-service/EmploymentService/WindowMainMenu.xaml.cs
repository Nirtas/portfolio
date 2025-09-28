using System.Windows;

namespace EmploymentService
{
	/// <summary>
	/// Логика взаимодействия для WindowMainMenu.xaml
	/// </summary>
	public partial class WindowMainMenu : Window
	{
		public WindowMainMenu()
		{
			InitializeComponent();

			Init();
		}

		void Init()
		{
			string accountType = WorkDatabase.CurrentUser.AccountType;

			if (accountType != null)
			{
				ButtonProfile.Visibility = Visibility.Visible;

				ButtonChangePassword.Visibility = Visibility.Visible;
			}
			else
			{
				LabelUnregisteredUser.Visibility = Visibility.Visible;
			}

		}

		private void ButtonExit_Click(object sender, RoutedEventArgs e)
		{
			WindowAuthorization windowAuthorization = new WindowAuthorization();

			windowAuthorization.Show();

			this.Close();
		}

		private void ButtonSearchVacancies_Click(object sender, RoutedEventArgs e)
		{
			WindowVacancies windowVacancies = new WindowVacancies();

			windowVacancies.Show();

			this.Close();
		}

		private void ButtonProfile_Click(object sender, RoutedEventArgs e)
		{
			WindowProfile windowProfile = new WindowProfile();

			windowProfile.Show();

			this.Close();
		}

		private void ButtonChangePassword_Click(object sender, RoutedEventArgs e)
		{
			WindowChangePassword windowChangePassword = new WindowChangePassword();

			windowChangePassword.ShowDialog();
		}
	}
}
