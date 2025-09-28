from matplotlib import pyplot as plt
from pandas import read_csv

# region Коэффициенты корреляции
correlation = plt.figure(figsize=(16, 11), num='Коэффициенты корреляции')

filenames = ['../DIP_LR1_kotlin/correlation/y = -10/correlationsRR.csv',
             '../DIP_LR1_kotlin/correlation/y = -5/correlationsRR.csv',
             '../DIP_LR1_kotlin/correlation/y = 0/correlationsRR.csv',
             '../DIP_LR1_kotlin/correlation/y = 5/correlationsRR.csv',
             '../DIP_LR1_kotlin/correlation/y = 10/correlationsRR.csv']
colors = ['blue', 'red', 'green', 'purple', 'black']
labels = ['y = -10', 'y = -5', 'y = 0', 'y = 5', 'y = 10']
plt.subplot(2, 3, 1)
for filename, color, label in zip(filenames, colors, labels):
    data = read_csv(filename, delimiter=';')
    data = data.sort_values(by='x')
    plt.plot(data['x'], data['coefficient'], color=color, label=label)
plt.xlabel('x')
plt.ylabel('Коэффициент корреляции')
plt.title('Коэффициент корреляции R')
plt.legend()

filenames = ['../DIP_LR1_kotlin/correlation/y = -10/correlationsGG.csv',
             '../DIP_LR1_kotlin/correlation/y = -5/correlationsGG.csv',
             '../DIP_LR1_kotlin/correlation/y = 0/correlationsGG.csv',
             '../DIP_LR1_kotlin/correlation/y = 5/correlationsGG.csv',
             '../DIP_LR1_kotlin/correlation/y = 10/correlationsGG.csv']
colors = ['blue', 'red', 'green', 'purple', 'black']
labels = ['y = -10', 'y = -5', 'y = 0', 'y = 5', 'y = 10']
plt.subplot(2, 3, 2)
for filename, color, label in zip(filenames, colors, labels):
    data = read_csv(filename, delimiter=';')
    data = data.sort_values(by='x')
    plt.plot(data['x'], data['coefficient'], color=color, label=label)
plt.xlabel('x')
plt.ylabel('Коэффициент корреляции')
plt.title('Коэффициент корреляции G')
plt.legend()

filenames = ['../DIP_LR1_kotlin/correlation/y = -10/correlationsBB.csv',
             '../DIP_LR1_kotlin/correlation/y = -5/correlationsBB.csv',
             '../DIP_LR1_kotlin/correlation/y = 0/correlationsBB.csv',
             '../DIP_LR1_kotlin/correlation/y = 5/correlationsBB.csv',
             '../DIP_LR1_kotlin/correlation/y = 10/correlationsBB.csv']
colors = ['blue', 'red', 'green', 'purple', 'black']
labels = ['y = -10', 'y = -5', 'y = 0', 'y = 5', 'y = 10']
plt.subplot(2, 3, 3)
for filename, color, label in zip(filenames, colors, labels):
    data = read_csv(filename, delimiter=';')
    data = data.sort_values(by='x')
    plt.plot(data['x'], data['coefficient'], color=color, label=label)
plt.xlabel('x')
plt.ylabel('Коэффициент корреляции')
plt.title('Коэффициент корреляции B')
plt.legend()

filenames = ['../DIP_LR1_kotlin/correlation/y = -10/correlationsYY.csv',
             '../DIP_LR1_kotlin/correlation/y = -5/correlationsYY.csv',
             '../DIP_LR1_kotlin/correlation/y = 0/correlationsYY.csv',
             '../DIP_LR1_kotlin/correlation/y = 5/correlationsYY.csv',
             '../DIP_LR1_kotlin/correlation/y = 10/correlationsYY.csv']
colors = ['blue', 'red', 'green', 'purple', 'black']
labels = ['y = -10', 'y = -5', 'y = 0', 'y = 5', 'y = 10']
plt.subplot(2, 3, 4)
for filename, color, label in zip(filenames, colors, labels):
    data = read_csv(filename, delimiter=';')
    data = data.sort_values(by='x')
    plt.plot(data['x'], data['coefficient'], color=color, label=label)
plt.xlabel('x')
plt.ylabel('Коэффициент корреляции')
plt.title('Коэффициент корреляции Y')
plt.legend()

filenames = ['../DIP_LR1_kotlin/correlation/y = -10/correlationsCbCb.csv',
             '../DIP_LR1_kotlin/correlation/y = -5/correlationsCbCb.csv',
             '../DIP_LR1_kotlin/correlation/y = 0/correlationsCbCb.csv',
             '../DIP_LR1_kotlin/correlation/y = 5/correlationsCbCb.csv',
             '../DIP_LR1_kotlin/correlation/y = 10/correlationsCbCb.csv']
colors = ['blue', 'red', 'green', 'purple', 'black']
labels = ['y = -10', 'y = -5', 'y = 0', 'y = 5', 'y = 10']
plt.subplot(2, 3, 5)
for filename, color, label in zip(filenames, colors, labels):
    data = read_csv(filename, delimiter=';')
    data = data.sort_values(by='x')
    plt.plot(data['x'], data['coefficient'], color=color, label=label)
plt.xlabel('x')
plt.ylabel('Коэффициент корреляции')
plt.title('Коэффициент корреляции Cb')
plt.legend()

filenames = ['../DIP_LR1_kotlin/correlation/y = -10/correlationsCrCr.csv',
             '../DIP_LR1_kotlin/correlation/y = -5/correlationsCrCr.csv',
             '../DIP_LR1_kotlin/correlation/y = 0/correlationsCrCr.csv',
             '../DIP_LR1_kotlin/correlation/y = 5/correlationsCrCr.csv',
             '../DIP_LR1_kotlin/correlation/y = 10/correlationsCrCr.csv']
colors = ['blue', 'red', 'green', 'purple', 'black']
labels = ['y = -10', 'y = -5', 'y = 0', 'y = 5', 'y = 10']
plt.subplot(2, 3, 6)
for filename, color, label in zip(filenames, colors, labels):
    data = read_csv(filename, delimiter=';')
    data = data.sort_values(by='x')
    plt.plot(data['x'], data['coefficient'], color=color, label=label)
plt.xlabel('x')
plt.ylabel('Коэффициент корреляции')
plt.title('Коэффициент корреляции Cr')
plt.legend()
# endregion

# region Гистограммы частот ORIGINAL
histogramOriginal = plt.figure(figsize=(16, 11), num='Гистограммы частот')

data = read_csv('../DIP_LR1_kotlin/histogram/R/original.csv', delimiter=';')
plt.subplot(2, 3, 1)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот R')

data = read_csv('../DIP_LR1_kotlin/histogram/G/original.csv', delimiter=';')
plt.subplot(2, 3, 2)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот G')

data = read_csv('../DIP_LR1_kotlin/histogram/B/original.csv', delimiter=';')
plt.subplot(2, 3, 3)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот B')

data = read_csv('../DIP_LR1_kotlin/histogram/Y/original.csv', delimiter=';')
plt.subplot(2, 3, 4)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Y')

data = read_csv('../DIP_LR1_kotlin/histogram/Cb/original.csv', delimiter=';')
plt.subplot(2, 3, 5)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cb')

data = read_csv('../DIP_LR1_kotlin/histogram/Cr/original.csv', delimiter=';')
plt.subplot(2, 3, 6)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cr')
# endregion

# region Гистограммы частот DPCM 1 RULE
histogramDPCM1 = plt.figure(figsize=(16, 11), num='Гистограммы частот DPCM 1 правило')

data = read_csv('../DIP_LR1_kotlin/histogram/R/DPCM 1 rule.csv', delimiter=';')
plt.subplot(2, 3, 1)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот R')

data = read_csv('../DIP_LR1_kotlin/histogram/G/DPCM 1 rule.csv', delimiter=';')
plt.subplot(2, 3, 2)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот G')

data = read_csv('../DIP_LR1_kotlin/histogram/B/DPCM 1 rule.csv', delimiter=';')
plt.subplot(2, 3, 3)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот B')

data = read_csv('../DIP_LR1_kotlin/histogram/Y/DPCM 1 rule.csv', delimiter=';')
plt.subplot(2, 3, 4)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Y')

data = read_csv('../DIP_LR1_kotlin/histogram/Cb/DPCM 1 rule.csv', delimiter=';')
plt.subplot(2, 3, 5)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cb')

data = read_csv('../DIP_LR1_kotlin/histogram/Cr/DPCM 1 rule.csv', delimiter=';')
plt.subplot(2, 3, 6)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cr')
# endregion

# region Гистограммы частот DPCM 2 RULE
histogramDPCM2 = plt.figure(figsize=(16, 11), num='Гистограммы частот DPCM 2 правило')

data = read_csv('../DIP_LR1_kotlin/histogram/R/DPCM 2 rule.csv', delimiter=';')
plt.subplot(2, 3, 1)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот R')

data = read_csv('../DIP_LR1_kotlin/histogram/G/DPCM 2 rule.csv', delimiter=';')
plt.subplot(2, 3, 2)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот G')

data = read_csv('../DIP_LR1_kotlin/histogram/B/DPCM 2 rule.csv', delimiter=';')
plt.subplot(2, 3, 3)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот B')

data = read_csv('../DIP_LR1_kotlin/histogram/Y/DPCM 2 rule.csv', delimiter=';')
plt.subplot(2, 3, 4)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Y')

data = read_csv('../DIP_LR1_kotlin/histogram/Cb/DPCM 2 rule.csv', delimiter=';')
plt.subplot(2, 3, 5)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cb')

data = read_csv('../DIP_LR1_kotlin/histogram/Cr/DPCM 2 rule.csv', delimiter=';')
plt.subplot(2, 3, 6)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cr')
# endregion

# region Гистограммы частот DPCM 3 RULE
histogramDPCM3 = plt.figure(figsize=(16, 11), num='Гистограммы частот DPCM 3 правило')

data = read_csv('../DIP_LR1_kotlin/histogram/R/DPCM 3 rule.csv', delimiter=';')
plt.subplot(2, 3, 1)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот R')

data = read_csv('../DIP_LR1_kotlin/histogram/G/DPCM 3 rule.csv', delimiter=';')
plt.subplot(2, 3, 2)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот G')

data = read_csv('../DIP_LR1_kotlin/histogram/B/DPCM 3 rule.csv', delimiter=';')
plt.subplot(2, 3, 3)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот B')

data = read_csv('../DIP_LR1_kotlin/histogram/Y/DPCM 3 rule.csv', delimiter=';')
plt.subplot(2, 3, 4)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Y')

data = read_csv('../DIP_LR1_kotlin/histogram/Cb/DPCM 3 rule.csv', delimiter=';')
plt.subplot(2, 3, 5)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cb')

data = read_csv('../DIP_LR1_kotlin/histogram/Cr/DPCM 3 rule.csv', delimiter=';')
plt.subplot(2, 3, 6)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cr')
# endregion

# region Гистограммы частот DPCM 4 RULE
histogramDPCM4 = plt.figure(figsize=(16, 11), num='Гистограммы частот DPCM 4 правило')

data = read_csv('../DIP_LR1_kotlin/histogram/R/DPCM 4 rule.csv', delimiter=';')
plt.subplot(2, 3, 1)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот R')

data = read_csv('../DIP_LR1_kotlin/histogram/G/DPCM 4 rule.csv', delimiter=';')
plt.subplot(2, 3, 2)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот G')

data = read_csv('../DIP_LR1_kotlin/histogram/B/DPCM 4 rule.csv', delimiter=';')
plt.subplot(2, 3, 3)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот B')

data = read_csv('../DIP_LR1_kotlin/histogram/Y/DPCM 4 rule.csv', delimiter=';')
plt.subplot(2, 3, 4)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Y')

data = read_csv('../DIP_LR1_kotlin/histogram/Cb/DPCM 4 rule.csv', delimiter=';')
plt.subplot(2, 3, 5)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cb')

data = read_csv('../DIP_LR1_kotlin/histogram/Cr/DPCM 4 rule.csv', delimiter=';')
plt.subplot(2, 3, 6)
plt.bar(data['x'], data['count'])
plt.xlabel('Значение компоненты')
plt.ylabel('Количество')
plt.title('Гистограмма частот Cr')
# endregion

correlation.savefig("../DIP_LR1_kotlin/correlation/correlations.png")
histogramOriginal.savefig("../DIP_LR1_kotlin/histogram/original.png")
histogramDPCM1.savefig("../DIP_LR1_kotlin/histogram/DPCM 1 rule.png")
histogramDPCM2.savefig("../DIP_LR1_kotlin/histogram/DPCM 2 rule.png")
histogramDPCM3.savefig("../DIP_LR1_kotlin/histogram/DPCM 3 rule.png")
histogramDPCM4.savefig("../DIP_LR1_kotlin/histogram/DPCM 4 rule.png")

plt.show()
