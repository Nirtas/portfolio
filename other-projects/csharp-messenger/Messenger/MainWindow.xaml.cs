using System.Windows;
using System.Windows.Controls;
using FireSharp;
using FireSharp.Response;

namespace Messenger
{
	/// <summary>
	/// Логика взаимодействия для MainWindow.xaml
	/// </summary>
	public partial class MainWindow : Window
	{
		Users user;
		Auth authWindow = new Auth();
		
		public MainWindow(Users curUser)
		{
			InitializeComponent();

			user = new Users
			{
				username = curUser.username,
				email = curUser.email,
				password = curUser.password,
				nickname = curUser.nickname,
				avatar = curUser.avatar
			};

			TbkProfileMenuNickname.Text = user.nickname;
		}

		private void Tb_GotFocus(object sender, RoutedEventArgs e)
		{
			SearchFocus(sender);
		}

		private void Tb_LostFocus(object sender, RoutedEventArgs e)
		{
			SearchFocus(sender);
		}

		private void SearchFocus(object sender)
		{
			if ((sender as TextBox).Text == "")
			{
				if ((sender as TextBox).Name == "TbSearchFriends")
					(sender as TextBox).Text = "Поиск собеседника...";
				if ((sender as TextBox).Name == "TbSearchMessage")
					(sender as TextBox).Text = "Поиск сообщения...";
				if ((sender as TextBox).Name == "TbChatMessage")
					(sender as TextBox).Text = "Текст сообщения...";
			}
			else if ((sender as TextBox).Text == "Поиск собеседника..." || (sender as TextBox).Text == "Поиск сообщения..." || (sender as TextBox).Text == "Текст сообщения...")
			{
				(sender as TextBox).Text = "";
			}
		}

		private void IconProfile_Click(object sender, RoutedEventArgs e)
		{
			MessageBox.Show("1");
		}

		private void UserProfile_Click(object sender, RoutedEventArgs e)
		{
			MessageBox.Show("2");
		}

		private void Button_Click_1(object sender, RoutedEventArgs e)
		{
			MessageBox.Show("Вы открыли меню собеседника.");
		}
		
		private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
		{
			Application.Current.Shutdown();
		}

		private void ButOpenProfileMenu_Click(object sender, RoutedEventArgs e)
		{
			GridProfileMenu.Visibility = Visibility.Visible;
		}

		private void ButCloseProfileMenu_Click(object sender, RoutedEventArgs e)
		{
			GridProfileMenu.Visibility = Visibility.Hidden;
		}

		private void ButProfileMenuChangeAvatar_Click(object sender, RoutedEventArgs e)
		{
			//?
		}

		private void ButProfileMenuChangeNickname_Click(object sender, RoutedEventArgs e)
		{
			ChangeNickname changeNickname = new ChangeNickname();
			changeNickname.ShowDialog();

			if (changeNickname.newNickname != TbkProfileMenuNickname.Text &&
			    !string.IsNullOrWhiteSpace(changeNickname.newNickname))
			{
				TbkProfileMenuNickname.Text = changeNickname.newNickname;
				user.nickname = changeNickname.newNickname;

				authWindow.fireResponse = authWindow.fireClient.Update(@"Users/" + user.username, user);
			}
			
		}
	}
}
