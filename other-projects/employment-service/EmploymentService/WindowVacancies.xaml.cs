using System;
using System.Collections.Generic;
using System.Data;
using System.Windows;
using System.Windows.Controls;

namespace EmploymentService
{
	/// <summary>
	/// Логика взаимодействия для WindowVacancies.xaml
	/// </summary>
	public partial class WindowVacancies : Window
	{
		DataTable vacancies = new DataTable();

		public WindowVacancies()
		{
			InitializeComponent();

			Init();
		}

		void Init()
		{
			try
			{
				string name;
				int pos = 0;

				List<string> tables = new List<string>() { "activity_fields", "operating_modes", "work_natures", "organizations", "posts" };

				for (int i = 0; i < 5; i++)
				{
					string sql = "SELECT * FROM " + tables[i] + ";";

					DataTable table = WorkDatabase.CheckDataSet(WorkDatabase.GetDataSet(sql));

					if (table != null)
					{
						name = tables[i];
						pos = 0;

						if (name.IndexOf('_') != -1)
						{
							pos = name.IndexOf('_');
							name = ReplaceString(name, pos, "");
							name = ReplaceString(name, pos, Char.ToUpper(name[pos]).ToString());
						}

						name = ReplaceString(name, 0, Char.ToUpper(name[0]).ToString());

						FillComboBox(String.Concat("ComboBox", name), table);
					}
				}
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString());
			}
		}

		string ReplaceString(string str, int pos, string symbol)
		{
			return str.Remove(pos, 1).Insert(pos, symbol);
		}

		void FillComboBox(string comboBoxName, DataTable table)
		{
			ComboBox comboBox = (ComboBox)this.FindName(comboBoxName);

			comboBox.Items.Clear();

			comboBox.Items.Add("Не выбрано");

			for (int i = 0; i < table.Rows.Count; i++)
			{
				comboBox.Items.Add(table.Rows[i][1].ToString());
			}

			comboBox.SelectedIndex = 0;
		}

		private void ButtonSearchVacancies_Click(object sender, RoutedEventArgs e)
		{
			string sql = "SELECT * FROM ";

			if ((bool)RadioButtonCurrentVacancies.IsChecked)
			{
				sql += "view_search_vacancies";

				ButtonVacancyApplicationStatus.Visibility = Visibility.Visible;

				LabelVacancyArchiveDate.Visibility = Visibility.Hidden;
				TextBoxVacancyArchiveDate.Visibility = Visibility.Hidden;

				LabelNumFreeSeats.Visibility = Visibility.Visible;
				TextBoxNumFreeSeats.Visibility = Visibility.Visible;
			}
			else
			{
				sql += "view_search_vacancies_archive";

				ButtonVacancyApplicationStatus.Visibility = Visibility.Hidden;

				LabelVacancyArchiveDate.Visibility = Visibility.Visible;
				TextBoxVacancyArchiveDate.Visibility = Visibility.Visible;

				LabelNumFreeSeats.Visibility = Visibility.Hidden;
				TextBoxNumFreeSeats.Visibility = Visibility.Hidden;
			}

			List<string> filterParts = new List<string>();

			#region filter
			if (ComboBoxActivityFields.SelectedIndex > 0)
			{
				filterParts.Add("`Сфера деятельности` = '" + ComboBoxActivityFields.Text + "'");
			}

			if (ComboBoxOperatingModes.SelectedIndex > 0)
			{
				filterParts.Add("`Режим работы` = '" + ComboBoxOperatingModes.Text + "'");
			}

			if (ComboBoxWorkNatures.SelectedIndex > 0)
			{
				filterParts.Add("`Характер работы` = '" + ComboBoxWorkNatures.Text + "'");
			}

			if (ComboBoxOrganizations.SelectedIndex > 0)
			{
				filterParts.Add("`Организация` = '" + ComboBoxOrganizations.Text + "'");
			}

			if (ComboBoxPosts.SelectedIndex > 0)
			{
				filterParts.Add("`Должность` = '" + ComboBoxPosts.Text + "'");
			}
			#endregion

			if (filterParts.Count > 0)
			{
				sql += " WHERE " + string.Join(" AND ", filterParts);
			}

			sql += " ORDER BY `ID вакансии`;";

			int countRows = WorkDatabase.GetCountRows(sql);

			if (countRows > 0)
			{
				GridVacancies.Height = 980;

				GridVacancy.Visibility = Visibility.Visible;

				LabelVacanciesNotFound.Visibility = Visibility.Hidden;

				LabelCountVacancies.Content = "Найдено вакансий: " + countRows;
			}
			else
			{
				GridVacancies.Height = 680;

				GridVacancy.Visibility = Visibility.Hidden;

				LabelVacanciesNotFound.Visibility = Visibility.Visible;

				vacancies = new DataTable();

				return;
			}

			vacancies = WorkDatabase.CheckDataSet(WorkDatabase.GetDataSet(sql));

			ButtonPrevVacancy.Visibility = Visibility.Hidden;

			ButtonNextVacancy.Visibility = countRows > 1 ? Visibility.Visible : Visibility.Hidden;

			if (WorkDatabase.CurrentUser.AccountType != "Employee")
			{
				ButtonVacancyApplicationStatus.Visibility = Visibility.Hidden;
			}

			FillVacancy(1);
		}

		void FillVacancy(int numVacancy)
		{
			LabelNumVacancy.Content = "Вакансия " + numVacancy;

			if (TextBoxVacancyArchiveDate.Visibility == Visibility.Visible)
			{
				TextBoxVacancyArchiveDate.Text = Convert.ToDateTime(vacancies.Rows[numVacancy - 1][0].ToString()).ToShortDateString();

				TextBoxPost.Text = vacancies.Rows[numVacancy - 1][2].ToString();
				TextBoxActivityField.Text = vacancies.Rows[numVacancy - 1][3].ToString();
			}
			else
			{
				if (WorkDatabase.CurrentUser.AccountType == "Employee")
				{
					if (WorkDatabase.CheckVacancyApplicationStatus(WorkDatabase.CurrentUser.IdMatch, numVacancy))
					{
						ButtonVacancyApplicationStatus.Content = "Убрать заявку на вакансию";
					}
					else
					{
						ButtonVacancyApplicationStatus.Content = "Оставить заявку на вакансию";
					}
				}

				TextBoxPost.Text = vacancies.Rows[numVacancy - 1][1].ToString();
				TextBoxActivityField.Text = vacancies.Rows[numVacancy - 1][2].ToString();
				TextBoxNumFreeSeats.Text = vacancies.Rows[numVacancy - 1][3].ToString();
			}

			TextBoxWorkNature.Text = vacancies.Rows[numVacancy - 1][4].ToString();
			TextBoxOrganization.Text = vacancies.Rows[numVacancy - 1][5].ToString();
			TextBoxReqCandidates.Text = vacancies.Rows[numVacancy - 1][6].ToString();
			TextBoxPayment.Text = vacancies.Rows[numVacancy - 1][7].ToString();
			TextBoxReqWorkExp.Text = vacancies.Rows[numVacancy - 1][8].ToString();
			TextBoxOperatingMode.Text = vacancies.Rows[numVacancy - 1][9].ToString();
			TextBoxCity.Text = vacancies.Rows[numVacancy - 1][10].ToString();
			TextBoxPhone.Text = vacancies.Rows[numVacancy - 1][11].ToString();
		}

		private void ButtonNextVacancy_Click(object sender, RoutedEventArgs e)
		{
			if (ButtonPrevVacancy.Visibility == Visibility.Hidden)
			{
				ButtonPrevVacancy.Visibility = Visibility.Visible;
			}

			int numVacancy = GetCurrentVacancy() + 1;

			int pos = LabelCountVacancies.Content.ToString().LastIndexOf(' ');

			int countVacancies = Convert.ToInt32(LabelCountVacancies.Content.ToString().Substring(++pos));

			if (numVacancy == countVacancies)
			{
				((Button)sender).Visibility = Visibility.Hidden;
			}

			FillVacancy(numVacancy);
		}

		private void ButtonPrevVacancy_Click(object sender, RoutedEventArgs e)
		{
			if (ButtonNextVacancy.Visibility == Visibility.Hidden)
			{
				ButtonNextVacancy.Visibility = Visibility.Visible;
			}

			int numVacancy = GetCurrentVacancy() - 1;

			if (numVacancy == 1)
			{
				((Button)sender).Visibility = Visibility.Hidden;
			}

			FillVacancy(numVacancy);
		}

		int GetCurrentVacancy()
		{
			int pos = LabelNumVacancy.Content.ToString().IndexOf(' ');

			return Convert.ToInt32(LabelNumVacancy.Content.ToString().Substring(++pos));
		}

		private void ButtonResetFilters_Click(object sender, RoutedEventArgs e)
		{
			ResetComboBox("ComboBoxActivityFields");
			ResetComboBox("ComboBoxOperatingModes");
			ResetComboBox("ComboBoxWorkNatures");
			ResetComboBox("ComboBoxOrganizations");
			ResetComboBox("ComboBoxPosts");

			RadioButtonCurrentVacancies.IsChecked = true;
		}

		void ResetComboBox(string comboBoxName)
		{
			ComboBox comboBox = (ComboBox)this.FindName(comboBoxName);

			comboBox.SelectedIndex = 0;
		}

		private void ButtonVacancyApplicationStatus_Click(object sender, RoutedEventArgs e)
		{
			string sql = "SELECT * FROM applicants_info WHERE Surname = 'Фамилия'";

			int countRows = WorkDatabase.GetCountRows(sql);

			if (countRows == 0)
			{
				if (WorkDatabase.ChangeVacancyApplicationStatus(WorkDatabase.CurrentUser.IdMatch, GetCurrentVacancy()))
				{
					ButtonVacancyApplicationStatus.Content = "Убрать заявку на вакансию";
				}
				else
				{
					ButtonVacancyApplicationStatus.Content = "Оставить заявку на вакансию";
				}
			}
			else
			{
				MessageBox.Show("Заполните свои данные в профиле корректно", "Ошибка");
			}
		}

		private void ButtonBack_Click(object sender, RoutedEventArgs e)
		{
			WindowMainMenu windowMainMenu = new WindowMainMenu();

			windowMainMenu.Show();

			this.Close();
		}
	}
}
