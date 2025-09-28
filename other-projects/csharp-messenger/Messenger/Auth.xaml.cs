using System;
using System.Collections.Generic;
using System.Windows;
using FireSharp;
using FireSharp.Config;
using FireSharp.Interfaces;
using FireSharp.Response;
using System.Windows.Threading;
using System.Net;
using System.Net.Mail;
using System.Windows.Controls;
using System.Windows.Input;

namespace Messenger
{
	/// <summary>
	/// Логика взаимодействия для Auth.xaml
	/// </summary>
	public partial class Auth : Window
	{
		DispatcherTimer timer;
		byte timerSec = 60;
		Random rand = new Random();
		int mailCode;
		public FirebaseResponse fireResponse;
		public SetResponse setResponse;
		public Users existUser;
		public Users curUser;
		string email;

		public IFirebaseConfig fireConfig = new FirebaseConfig
		{
			AuthSecret = "iYH2jLsw2aLxlzQCGjTyDoDP2Y208zbZSirADEP3",
			BasePath = "https://messenger-b4834.firebaseio.com/"
		};

		public IFirebaseClient fireClient;

		public Auth()
		{
			InitializeComponent();

			try
			{
				fireClient = new FirebaseClient(fireConfig);
			}
			catch
			{
				MessageBox.Show("Ошибка. Проверьте подключение к Интернету.", "Ошибка");
			}
		}

		private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
		{
			Application.Current.Shutdown();
		}

		private void TbAuthUsername_TextChanged(object sender, TextChangedEventArgs e)
		{
			TbAuth_TextChanged();
		}

		private void TbAuthPassword_PasswordChanged(object sender, RoutedEventArgs e)
		{
			TbAuth_TextChanged();
		}

		private void TbAuth_TextChanged()
		{
			if (!string.IsNullOrWhiteSpace(TbAuthUsername.Text) && !string.IsNullOrWhiteSpace(PbAuthPassword.Password))
			{
				ButAuthNext.IsEnabled = true;
			}
			else
			{
				ButAuthNext.IsEnabled = false;
			}
		}

		private void ButAuthNext_Click(object sender, RoutedEventArgs e)
		{
			fireResponse = fireClient.Get(@"Users/" + TbAuthUsername.Text);
			existUser = fireResponse.ResultAs<Users>();

			/* Шифрование */

			curUser = new Users
			{
				username = TbAuthUsername.Text,
				password = PbAuthPassword.Password
			};

			byte checkResult;

			if (existUser == null)
			{
				checkResult = 0;
			}
			else
			{
				checkResult = Users.EqualCheck(existUser, curUser);
			}

			if (checkResult == 0)
			{
				ImAuthMessengerIcon.Visibility = Visibility.Hidden;
				ImAuthRegIcon.Visibility = Visibility.Visible;
				TbkWelcome.Visibility = Visibility.Hidden;
				TbkAuthReg.Visibility = Visibility.Visible;
				TbAuthEmail.Visibility = Visibility.Visible;
				TbkAuthUsername.Visibility = Visibility.Hidden;
				TbAuthUsername.Visibility = Visibility.Hidden;
				TbkAuthPassword.Visibility = Visibility.Hidden;
				PbAuthPassword.Visibility = Visibility.Hidden;
				ButAuthNext.Visibility = Visibility.Hidden;
				ButAuthBack.Visibility = Visibility.Visible;
				ButAuthFinish.Visibility = Visibility.Visible;
				ButAuthSendCode.Visibility = Visibility.Visible;
			}
			else if (checkResult == 1)
			{
				MessageBox.Show("Неправильный пароль!", "Ошибка");
			}
			else if (checkResult == 2)
			{
				/*curUser = new Users
				{
					username = existUser.username,
					email = existUser.email,
					password = existUser.password,
					nickname = existUser.nickname,
					avatar = existUser.avatar
				};*/

				curUser.email = existUser.email;
				curUser.nickname = existUser.nickname;
				curUser.avatar = existUser.avatar;

				OpenMainWindow();
			}
			else
			{
				MessageBox.Show("Попробуйте еще раз", "Ошибка");
			}
		}

		private void OpenMainWindow()
		{
			MainWindow mainWindow = new MainWindow(curUser);
			mainWindow.Show();
			this.Hide();
		}

		private void ButAuthFinish_Click(object sender, RoutedEventArgs e)
		{
			if (TbAuthCode.Text == mailCode.ToString())
			{
				if (timer != null)
				{
					TimerStop();
				}

				curUser.email = email;
				curUser.avatar = "Default/avatar.png";
				curUser.nickname = curUser.username;

				setResponse = fireClient.Set(@"Users/" + curUser.username, curUser);

				OpenMainWindow();
			}
			else
			{
				MessageBox.Show("Неверный код", "Ошибка");
			}
		}

		private void ButAuthBack_Click(object sender, RoutedEventArgs e)
		{
			ImAuthMessengerIcon.Visibility = Visibility.Visible;
			ImAuthRegIcon.Visibility = Visibility.Hidden;
			TbkWelcome.Visibility = Visibility.Visible;
			TbkAuthReg.Visibility = Visibility.Hidden;
			TbAuthEmail.Visibility = Visibility.Hidden;
			TbkAuthUsername.Visibility = Visibility.Visible;
			TbAuthUsername.Visibility = Visibility.Visible;
			TbkAuthPassword.Visibility = Visibility.Visible;
			PbAuthPassword.Visibility = Visibility.Visible;
			ButAuthNext.Visibility = Visibility.Visible;
			ButAuthBack.Visibility = Visibility.Hidden;
			ButAuthFinish.Visibility = Visibility.Hidden;
			ButAuthSendCode.Visibility = Visibility.Hidden;
			TbkAuthRegCodeTime.Visibility = Visibility.Hidden;
			TbkAuthCode.Visibility = Visibility.Hidden;
			TbAuthCode.Visibility = Visibility.Hidden;

			if ((string)ButAuthSendCode.Content == "Выслать повторно")
			{
				if (!string.IsNullOrWhiteSpace(TbAuthEmail.Text))
					ButAuthSendCode.IsEnabled = true;

				ButAuthSendCode.Content = "Подтвердить";
			}

			if (timer != null)
			{
				TimerStop();
			}
		}

		private void ButAuthSendCode_Click(object sender, RoutedEventArgs e)
		{
			fireResponse = fireClient.Get(@"Users");
			Dictionary<string, Users> getUsers = fireResponse.ResultAs<Dictionary<string, Users>>();

			foreach (var user in getUsers)
			{
				if (user.Value.email == TbAuthEmail.Text)
				{
					MessageBox.Show("Адрес электронной почты уже зарегистрирован", "Ошибка");
					return;
				}
			}

			try
			{
				MailAddress fromAddress = new MailAddress("messenger.app.ru@gmail.com", "Messenger");
				MailAddress toAddress = new MailAddress(TbAuthEmail.Text);
				MailMessage mailMessage = new MailMessage(fromAddress, toAddress);

				mailCode = rand.Next(100000, 1000000);

				mailMessage.Body = mailCode.ToString();
				mailMessage.Subject = "Подтверждение регистрации в Messenger";

				SmtpClient smtpClient = new SmtpClient();
				smtpClient.Host = "smtp.gmail.com";
				smtpClient.Port = 587;
				smtpClient.EnableSsl = true;
				smtpClient.DeliveryMethod = SmtpDeliveryMethod.Network;
				smtpClient.UseDefaultCredentials = false;
				smtpClient.Credentials = new NetworkCredential(fromAddress.Address, "d=sGwH%kT3C6aCPz");

				smtpClient.Send(mailMessage);
			}
			catch
			{
				MessageBox.Show("Введите корректный адрес электронной почты!", "Ошибка");
				return;
			}

			email = TbAuthEmail.Text;
			ButAuthSendCode.Content = "Выслать повторно";
			ButAuthSendCode.IsEnabled = false;
			TbkAuthCode.Visibility = Visibility.Visible;
			TbAuthCode.Visibility = Visibility.Visible;
			TbkAuthRegCodeTime.Visibility = Visibility.Visible;
			TimerStart();
		}

		private void TbAuthEmail_TextChanged(object sender, TextChangedEventArgs e)
		{
			if (!string.IsNullOrWhiteSpace(TbAuthEmail.Text))
			{
				ButAuthSendCode.IsEnabled = true;
			}
			else
			{
				ButAuthSendCode.IsEnabled = false;
			}

			if (timer != null && timer.IsEnabled)
				ButAuthSendCode.IsEnabled = false;
		}

		private void TimerStart()
		{
			timer = new DispatcherTimer();
			timer.Tick += new EventHandler(TimerTick);
			timer.Interval = new TimeSpan(0, 0, 0, 1);
			timer.IsEnabled = true;
			timer.Start();
		}

		private void TimerTick(object sender, EventArgs e)
		{
			timerSec--;
			TbkAuthRegCodeTime.Text = "Повторная отправка письма возможна через " + timerSec + " сек.";

			if (timerSec == 0)
			{
				TimerStop();
				ButAuthSendCode.IsEnabled = true;
				TbkAuthRegCodeTime.Visibility = Visibility.Hidden;
			}
		}

		private void TimerStop()
		{
			timer.Stop();
			timer.IsEnabled = false;
			timerSec = 60;
			TbkAuthRegCodeTime.Text = "Повторная отправка письма возможна через " + timerSec + " сек.";
		}

		private void TbAuthCode_TextChanged(object sender, TextChangedEventArgs e)
		{
			if (!string.IsNullOrWhiteSpace(TbAuthCode.Text))
			{
				ButAuthFinish.IsEnabled = true;
			}
			else
			{
				ButAuthFinish.IsEnabled = false;
			}
		}

		private void TbAuth_PreviewKeyDown(object sender, KeyEventArgs e)
		{
			if (e.Key == Key.Space)
			{
				e.Handled = true;
			}
		}

		private void DeleteSpaces(object sender)
		{
			(sender as TextBox).Text = (sender as TextBox).Text.Replace(" ", "");
		}

		private void TbAuth_LostFocus(object sender, RoutedEventArgs e)
		{
			DeleteSpaces(sender);
		}
	}
}
