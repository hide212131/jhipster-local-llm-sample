export interface IUploadedFile {
  id?: number;
  fileId?: string;
  filename?: string;
  dataContentType?: string;
  data?: string;
}

export const defaultValue: Readonly<IUploadedFile> = {};
