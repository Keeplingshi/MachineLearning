import kNN

# print(kNN.handwriting_class_est())
group, labels = kNN.create_data_set()
print(kNN.classify0([0.3, 0.3], group, labels, 3))
