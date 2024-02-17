import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UploadedFile from './uploaded-file';
import UploadedFileDetail from './uploaded-file-detail';
import UploadedFileUpdate from './uploaded-file-update';
import UploadedFileDeleteDialog from './uploaded-file-delete-dialog';

const UploadedFileRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UploadedFile />} />
    <Route path="new" element={<UploadedFileUpdate />} />
    <Route path=":id">
      <Route index element={<UploadedFileDetail />} />
      <Route path="edit" element={<UploadedFileUpdate />} />
      <Route path="delete" element={<UploadedFileDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UploadedFileRoutes;
